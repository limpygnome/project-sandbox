package com.projectsandbox.components.server.map.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.spawn.Spawn;
import com.projectsandbox.components.server.map.component.helper.SpawnParserHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * Created by limpygnome on 18/07/16.
 */
@Component
public class EntitiesMapComponent implements MapComponent
{
    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;
    @Autowired
    private RespawnManager respawnManager;
    @Autowired
    private SpawnParserHelper spawnParserHelper;

    @Override
    public void load(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        // TODO: we need to look at serialization/deserialization of worldmap instead...
        loadEntities(controller, mapData, map);
    }

    @Override
    public void persist(Controller controller, JSONObject rootObject, WorldMap map)
    {
    }

    protected void loadEntities(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        JSONArray rawEntities = (JSONArray) mapData.get("entities");
        JSONObject rawEntity;

        for (Object rawEntityObject : rawEntities)
        {
            rawEntity = (JSONObject) rawEntityObject;
            loadEntity(controller, map, rawEntity);
        }
    }

    protected void loadEntity(Controller controller, WorldMap map, JSONObject entData) throws IOException
    {
        // Parse faction
        short faction = (short) (long) entData.get("faction");

        // Parse spawn (optional)
        Spawn spawn = spawnParserHelper.parseSpawn((JSONObject) entData.get("spawn"));

        // Parse KV for spawning ent
        JSONObject rawKV = (JSONObject) entData.get("properties");
        MapEntKV mapEntKV;

        if (rawKV != null)
        {
            mapEntKV = loadEntityProperties(rawKV);
        }
        else
        {
            mapEntKV = null;
        }

        // Create instance
        Entity entity;

        if (entData.containsKey("typeName"))
        {
            String typeName = (String) entData.get("typeName");
            entity = entityTypeMappingStoreService.createByTypeName(typeName, mapEntKV);
        }
        else if (entData.containsKey("typeId"))
        {
            short typeId = (short) (long) entData.get("typeId");
            entity = entityTypeMappingStoreService.createByTypeId(typeId, mapEntKV);
        }
        else
        {
            throw new RuntimeException("No type defined for entity in map file");
        }

        // Set parameters
        entity.faction = faction;
        entity.spawn = spawn;

        // Add to world
        respawnManager.respawn(new EntityPendingRespawn(controller, map, entity, 0, false));
    }

    private MapEntKV loadEntityProperties(JSONObject rawProperties)
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

}
