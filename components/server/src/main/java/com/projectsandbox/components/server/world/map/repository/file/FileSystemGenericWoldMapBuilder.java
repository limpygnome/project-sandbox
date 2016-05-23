package com.projectsandbox.components.server.world.map.repository.file;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import com.projectsandbox.components.server.world.spawn.FactionSpawns;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * A generic map builder for parsing map data from JSON.
 */
public abstract class FileSystemGenericWoldMapBuilder implements FileSystemMapBuilder
{
    private final static Logger LOG = LogManager.getLogger(FileSystemGenericWoldMapBuilder.class);

    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

    @Override
    public WorldMap build(Controller controller, MapService mapService, JSONObject mapData) throws IOException
    {
        // Parse unique identifier...
        short mapId = (short) (long) mapData.get("id");

        // Create new instance
        WorldMap map = createMapInstance(controller, mapService, mapId);

        if (map == null)
        {
            throw new RuntimeException("Implementation did not create map instance...");
        }

        // Build parts of map from JSON data
        buildMapProperties(map, mapData);

        if (!map.getProperties().isEnabled())
        {
            LOG.warn("Map not enabled, skipped loading - id: {}", mapId);
            return null;
        }

        buildFactionSpawns(controller, mapData, map);
        buildEntities(controller, mapData, map);

        return map;
    }

    protected void buildMapProperties(WorldMap map, JSONObject mapData)
    {
        JSONObject rawProperties = (JSONObject) mapData.get("properties");

        // Read properties
        WorldMapProperties properties = createPropertiesInstance();

        properties.setName((String) rawProperties.get("name"));
        properties.setEnabled((boolean) rawProperties.get("enabled"));
        properties.setLobby((boolean) rawProperties.get("lobby"));

        // -- Read type and fetch from ent mappings
        String entityTypeName = (String) rawProperties.get("defaultPlayerEntity");
        Class clazz = entityTypeMappingStoreService.getEntityClassByTypeName(entityTypeName);

        if (clazz == null)
        {
            throw new RuntimeException("Unable to use '" + entityTypeName + "' as default player entity, type does not exist - map id: " + map.getMapId());
        }

        properties.setDefaultEntityType(clazz);

        // Set map with properties loaded
        map.setProperties(properties);
    }

    protected void buildFactionSpawns(Controller controller, JSONObject mapData, WorldMap map)
    {
        JSONArray rawSpawnData = (JSONArray) mapData.get("factionSpawns");
        JSONObject rawFactionSpawns;

        for (Object factionData : rawSpawnData)
        {
            rawFactionSpawns = (JSONObject) factionData;
            buildFactionSpawn(controller, map, rawFactionSpawns);
        }
    }

    protected void buildFactionSpawn(Controller controller, WorldMap map, JSONObject factionData)
    {
        short factionId = (short) (long) factionData.get("id");

        FactionSpawns factionSpawns = new FactionSpawns(factionId);

        // Parse spawns
        JSONArray spawnsData = (JSONArray) factionData.get("spawns");

        if (spawnsData != null)
        {
            Spawn spawn;
            for (Object spawnData : spawnsData)
            {
                spawn = parseSpawn((JSONObject) spawnData);
                factionSpawns.addSpawn(spawn);
            }
        }

        // Add to map
        // TODO: should add to "spawnData" in this map...
        map.respawnManager.factionSpawnsAdd(map.getMapId(), factionSpawns);
    }

    protected void buildEntities(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        JSONArray rawEntities = (JSONArray) mapData.get("entities");
        JSONObject rawEntity;

        for (Object rawEntityObject : rawEntities)
        {
            rawEntity = (JSONObject) rawEntityObject;
            buildEntity(controller, map, rawEntity);
        }
    }

    protected void buildEntity(Controller controller, WorldMap map, JSONObject entData) throws IOException
    {
        // Fetch ent type - either by ID or name
        Class entClass;

        if (entData.containsKey("typeName"))
        {
            entClass = entityTypeMappingStoreService.getEntityClassByTypeName((String) entData.get("typeName"));
        }
        else if (entData.containsKey("typeId"))
        {
            entClass = entityTypeMappingStoreService.getEntityClassByTypeId((short) (long) entData.get("typeId"));
        }
        else
        {
            throw new RuntimeException("No type defined for entity in map file");
        }

        // Check class was found
        if (entClass == null)
        {
            throw new IOException("Entity type not found - typeID: " + entData.get("typeId") + ", typeName: " +
                    entData.get("typeName"));
        }

        // Parse faction
        short faction = (short) (long) entData.get("faction");

        // Parse spawn (optional)
        Spawn spawn = parseSpawn((JSONObject) entData.get("spawn"));

        // Parse count to spawn / instances to create
        long count = (long) entData.get("count");

        // Parse KV for spawning ent
        JSONObject rawKV = (JSONObject) entData.get("properties");
        MapEntKV mapEntKV;

        if (rawKV != null)
        {
            mapEntKV = parseEntityProperties(rawKV);
        }
        else
        {
            mapEntKV = null;
        }

        // Create new instances of type
        buildEntityInstances(controller, map, entClass, mapEntKV, count, faction, spawn);
    }

    protected static MapEntKV parseEntityProperties(JSONObject rawProperties)
    {
        // TODO: refactor away from "KV" naming
        MapEntKV mapEntKV = new MapEntKV();

        // Parse each KV
        Iterator iterator = rawProperties.entrySet().iterator();
        java.util.Map.Entry<Object, Object> kv;
        String key;
        String value;

        while (iterator.hasNext())
        {
            // Read KV
            kv = (java.util.Map.Entry<Object, Object>) iterator.next();
            key = (String) kv.getKey();
            value = kv.getValue().toString();

            // Add to map
            mapEntKV.put(key, value);
        }

        return mapEntKV;
    }

    protected static void buildEntityInstances(Controller controller, WorldMap map, Class entClass, MapEntKV mapEntKV, long count, short faction, Spawn spawn) throws IOException
    {
        boolean useKv = (mapEntKV != null);

        // Fetch constructor
        Constructor entConstructor;

        try
        {
            if (useKv)
            {
                entConstructor = entClass.getConstructor(WorldMap.class, MapEntKV.class);
            }
            else
            {
                entConstructor = entClass.getConstructor(WorldMap.class);
            }
        }
        catch (NoSuchMethodException e)
        {
            if (mapEntKV != null)
            {
                throw new RuntimeException("Entity constructor missing for KV loading from map - class: " + entClass.getName(), e);
            }
            else
            {
                throw new RuntimeException("Entity class does not contain default constructor - class: " + entClass.getName(), e);
            }
        }

        // Create ents
        Entity entity;

        for (int i = 0; i < count; i++)
        {
            try
            {
                // Create instance
                if (useKv)
                {
                    entity = (Entity) entConstructor.newInstance(map, mapEntKV);
                }
                else
                {
                    entity = (Entity) entConstructor.newInstance(map);
                }

                // Set parameters
                entity.faction = faction;
                entity.spawn = spawn;
            }
            catch (Exception e)
            {
                throw new IOException("Unable to create entity instance - class: " + entClass.getName(), e);
            }

            // Add to world
            map.respawnManager.respawn(new EntityPendingRespawn(controller, entity));
        }
    }

    protected static Spawn parseSpawn(JSONObject spawn)
    {
        if (spawn != null)
        {
            float x = (float) (double) spawn.get("x");
            float y = (float) (double) spawn.get("y");
            float rotation = (float) (double) spawn.get("rotation");

            return new Spawn(x, y, rotation);
        }
        else
        {
            return null;
        }
    }

}
