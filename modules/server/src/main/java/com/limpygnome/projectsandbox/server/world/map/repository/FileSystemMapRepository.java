package com.limpygnome.projectsandbox.server.world.map.repository;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.packet.imp.map.MapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.world.map.*;
import com.limpygnome.projectsandbox.server.world.spawn.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Used to load maps from a file-system or the class-path.
 */
@Repository
public class FileSystemMapRepository implements MapRepository
{
    private final static Logger LOG = LogManager.getLogger(FileSystemMapRepository.class);

    @Override
    public Map<Short, WorldMap> fetchPublicMaps(Controller controller, MapManager mapManager)
    {
        Map<Short, WorldMap> maps = new HashMap<>();

        try
        {
            FileSystemFile[] files = FileSystem.getResources(PathConstants.BASE_PACKAGE_MAPS);

            // Iterate and load each map file
            JSONObject mapData;
            WorldMap map;

            for (FileSystemFile file : files)
            {
                // Load map data
                mapData = JsonHelper.read(file.getInputStream());

                // Build map using data
                map = buildMap(controller, mapManager, mapData);

                // Add to result
                maps.put(map.mapId, map);

                LOG.debug("loaded public map - {}", map);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to load common maps", e);
        }

        return maps;
    }

    @Override
    public WorldMap fetchMap(Controller controller, MapManager mapManager, UUID uuid)
    {
        throw new RuntimeException("no support for loading individual maps");
    }

    private WorldMap buildMap(Controller controller, MapManager mapManager, JSONObject mapData) throws IOException
    {
        // Parse unique identifier...
        short mapId = (short) (long) mapData.get("id");

        // Create new instance
        WorldMap map = new WorldMap(controller, mapManager, mapId);

        // Build parts of map from JSON data
        buildMapProperties(map, mapData);
        buildTileTypesAndTiles(controller, mapData, map);
        buildFactionSpawns(controller, mapData, map);
        buildEntities(controller, mapData, map);

        // Build map packet
        // TODO: reconsider why we do this, or how it could be better / automatic, perhaps move into tileData? Or it triggers it?
        map.packet = new MapDataOutboundPacket();
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
        WorldMapTileData tileData = new WorldMapTileData(map);

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

    private void buildTileProperties(WorldMapTileData tileData, JSONObject mapData)
    {
        JSONObject rawTileProperties = (JSONObject) mapData.get("tile_properties");

        // Parse tile properties
        tileData.tileSize = (float) (long) rawTileProperties.get("tile_size");
        tileData.tileSizeHalf = tileData.tileSize / 2.0f;
        tileData.tileSizeQuarter = tileData.tileSize / 4.0f;
        tileData.widthTiles = (short) (long) rawTileProperties.get("tiles_width");
        tileData.heightTiles = (short) (long) rawTileProperties.get("tiles_height");

        // Compute max boundaries
        tileData.maxX = tileData.tileSize * (float) tileData.widthTiles;
        tileData.maxY = tileData.tileSize * (float) tileData.heightTiles;
    }

    private TileType[] buildTileTypes(Controller controller, JSONObject mapData) throws IOException
    {
        // Fetch tile types
        JSONArray rawTileTypes = (JSONArray) mapData.get("tile_types");

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

    private void buildTiles(WorldMap map, Map<String, TileType> tileTypeMap, JSONObject mapData, WorldMapTileData tileData) throws IOException
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
        controller.respawnManager.factionSpawnsAdd(map.mapId, factionSpawns);
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
            entClass = controller.entityManager.entTypeMappingStore.getEntityClassByTypeName((String) entData.get("typeName"));
        }
        else if (entData.containsKey("typeId"))
        {
            entClass = controller.entityManager.entTypeMappingStore.getEntityClassByTypeId((short) (long) entData.get("typeId"));
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
                entConstructor = entClass.getConstructor(MapEntKV.class);
            }
            else
            {
                entConstructor = entClass.getConstructor();
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
                    entity = (Entity) entConstructor.newInstance(mapEntKV);
                }
                else
                {
                    entity = (Entity) entConstructor.newInstance();
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
            controller.respawnManager.respawn(new EntityPendingRespawn(controller, entity));
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
