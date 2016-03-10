package com.limpygnome.projectsandbox.server.world.map.repository.file;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.world.map.MapEntKV;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;
import com.limpygnome.projectsandbox.server.world.map.packet.TileMapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.world.map.tile.TileData;
import com.limpygnome.projectsandbox.server.world.map.tile.TileType;
import com.limpygnome.projectsandbox.server.world.map.tile.TileWorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation for {@link TileWorldMap}.
 */
public class FileSystemTileWorldMapBuilder implements FileSystemMapBuilder
{

    @Override
    public WorldMap build(Controller controller, MapService mapService, JSONObject mapData) throws IOException
    {
        // Parse unique identifier...
        short mapId = (short) (long) mapData.get("id");

        // Create new instance
        WorldMap map = new TileWorldMap(controller, mapService, mapId);

        // Build parts of map from JSON data
        buildMapProperties(map, mapData);
        buildTileTypesAndTiles(controller, mapData, map);
        buildFactionSpawns(controller, mapData, map);
        buildEntities(controller, mapData, map);

        // Build map packet
        // TODO: reconsider why we do this, or how it could be better / automatic, perhaps move into tileData? Or it triggers it?
        map.packet = new TileMapDataOutboundPacket();
        map.packet.build(map);

        return map;
    }

    private void buildMapProperties(WorldMap map, JSONObject mapData)
    {
        WorldMapProperties properties = new WorldMapProperties();

        // Fetch properties node from map data
        JSONObject rawProperties = (JSONObject) mapData.get("properties");

        properties.name = (String) rawProperties.get("name");
        properties.lobby = (boolean) rawProperties.get("lobby");

        // Set map with properties loaded
        map.properties = properties;
    }

    private void buildTileTypesAndTiles(Controller controller, JSONObject mapData, WorldMap map)
            throws IOException
    {
        TileData tileData = new TileData(map);

        // Load tile properties
        buildTileProperties(tileData, mapData);

        // Load tile-type data
        TileType[] tileTypes = buildTileTypes(controller, mapData);
        tileData.tileTypes = tileTypes;

        // Create map to speed-up 'tile-name -> ID' translation
        Map<String, TileType> tileTypeByNameMappings = buildTileTypeMap(tileTypes);

        // Load tiles
        buildTiles(map, tileTypeByNameMappings, mapData, tileData);

        // Set map tile data
        map.tileData = tileData;
    }

    private void buildTileProperties(TileData tileData, JSONObject mapData)
    {
        JSONObject rawTileProperties = (JSONObject) mapData.get("tileProperties");

        // Parse tile properties
        tileData.tileSize = (float) (long) rawTileProperties.get("tileSize");
        tileData.tileSizeHalf = tileData.tileSize / 2.0f;
        tileData.tileSizeQuarter = tileData.tileSize / 4.0f;
        tileData.widthTiles = (short) (long) rawTileProperties.get("tilesWidth");
        tileData.heightTiles = (short) (long) rawTileProperties.get("tilesHeight");

        // Compute max boundaries
        tileData.maxX = tileData.tileSize * (float) tileData.widthTiles;
        tileData.maxY = tileData.tileSize * (float) tileData.heightTiles;
    }

    private TileType[] buildTileTypes(Controller controller, JSONObject mapData) throws IOException
    {
        // Fetch tile types
        JSONArray rawTileTypes = (JSONArray) mapData.get("tileTypes");

        // Parse tile types into efficient array, whereby tile ID is the index in the array
        TileType[] tileTypes = new TileType[rawTileTypes.size()];

        JSONObject arrayObj;
        TileType tileType;

        short tileTypeIdCounter = 0;

        for (Object rawTileType : rawTileTypes)
        {
            // Parse tile type
            arrayObj = (JSONObject) rawTileType;
            tileType = TileType.load(controller, arrayObj);

            // Assign ID to type
            tileType.id = tileTypeIdCounter++;

            // Assign to array
            tileTypes[tileType.id] = tileType;
        }

        return tileTypes;
    }

    private Map<String, TileType> buildTileTypeMap(TileType[] tileTypes)
    {
        Map<String, TileType> tileTypeMap = new HashMap<>(tileTypes.length);

        for (TileType tileType : tileTypes)
        {
            tileTypeMap.put(tileType.name, tileType);
        }

        return tileTypeMap;
    }

    private void buildTiles(WorldMap map, Map<String, TileType> tileTypeMap, JSONObject mapData, TileData tileData) throws IOException
    {
        JSONArray tiles = (JSONArray) mapData.get("tiles");

        // Setup tiles array
        tileData.tiles = new short[tileData.heightTiles][tileData.widthTiles];

        // Setup vertices array
        tileData.tileVertices = new Vertices[tileData.heightTiles][tileData.widthTiles];

        // Parse tiles
        int yOffset = 0;
        String tile;
        short typeIndex;
        TileType type;

        // -- Note: y is inverted since 0 is bottom and x is top!
        for(int y = tileData.heightTiles - 1; y >= 0; y--)
        {
            for(int x = 0; x < tileData.widthTiles; x++)
            {
                // Fetch tile
                tile = (String) tiles.get(yOffset++);

                // Locate actual type
                typeIndex = tileTypeMap.get(tile).id;
                type = tileData.tileTypes[typeIndex];

                if(type == null)
                {
                    throw new IOException("Failed to find tile '" + tile +
                            "' [x: " + x + ", y: " + y + "] for map '" +
                            map.properties.name + "'"
                    );
                }

                // Assign type
                tileData.tiles[y][x] = type.id;

                // Build vertices
                tileData.tileVertices[y][x] = Vertices.buildTileVertices(tileData, x, y);
            }
        }
    }

    private void buildFactionSpawns(Controller controller, JSONObject mapData, WorldMap map)
    {
        JSONArray rawSpawnData = (JSONArray) mapData.get("factionSpawns");
        JSONObject rawFactionSpawns;

        for (Object factionData : rawSpawnData)
        {
            rawFactionSpawns = (JSONObject) factionData;
            buildFactionSpawn(controller, map, rawFactionSpawns);
        }
    }

    private void buildFactionSpawn(Controller controller, WorldMap map, JSONObject factionData)
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
        map.respawnManager.factionSpawnsAdd(map.mapId, factionSpawns);
    }

    private void buildEntities(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        JSONArray rawEntities = (JSONArray) mapData.get("entities");
        JSONObject rawEntity;

        for (Object rawEntityObject : rawEntities)
        {
            rawEntity = (JSONObject) rawEntityObject;
            buildEntity(controller, map, rawEntity);
        }
    }

    private void buildEntity(Controller controller, WorldMap map, JSONObject entData) throws IOException
    {
        // Fetch ent type - either by ID or name
        Class entClass;

        if (entData.containsKey("typeName"))
        {
            entClass = map.entityManager.entTypeMappingStoreService.getEntityClassByTypeName((String) entData.get("typeName"));
        }
        else if (entData.containsKey("typeId"))
        {
            entClass = map.entityManager.entTypeMappingStoreService.getEntityClassByTypeId((short) (long) entData.get("typeId"));
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

    private static MapEntKV parseEntityProperties(JSONObject rawProperties)
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

    private static void buildEntityInstances(Controller controller, WorldMap map, Class entClass, MapEntKV mapEntKV, long count, short faction, Spawn spawn) throws IOException
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

    private static Spawn parseSpawn(JSONObject spawn)
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
