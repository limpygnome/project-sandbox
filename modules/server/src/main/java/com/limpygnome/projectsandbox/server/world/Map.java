package com.limpygnome.projectsandbox.server.world;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.packet.imp.map.MapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.entity.Entity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Holds data regarding a map.
 */
public class Map
{
    private final static Logger LOG = LogManager.getLogger(Map.class);

    private final Controller controller;
    public final short mapId;

    private short tileTypeIdCounter;
    public HashMap<String, Short> tileNameToTypeIndexMappings;
    public TileType[] tileTypes;

    private final MapManager mapManager;
    
    public String name;
    public short tileSize;
    public short width;
    public short height;
    public float maxX;
    public float maxY;
    
    /** Mapped by [y][x] or [row][column]; bottom-left at 0, top-right at n */
    public short tiles[][];
    public Vertices[][] tileVertices;

    /* Pre-computed/cached values */
    public float tileSizeHalf;
    public float tileSizeQuarter;

    // if this is later updated elsewhere, it needs thread protection
    public MapDataOutboundPacket packet;
    
    private Map(Controller controller, MapManager mapManager, short mapId)
    {
        this.controller = controller;
        this.mapManager = mapManager;
        this.mapId = mapId;
        this.tileNameToTypeIndexMappings = new HashMap<>();
        this.tileTypeIdCounter = 0;
    }

    public MapPosition positionFromReal(Vector2 vector)
    {
        return positionFromReal(vector.x, vector.y);
    }

    public MapPosition positionFromReal(float x, float y)
    {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        return new MapPosition(
                this,
                x,
                y,
                tileX,
                tileY
        );
    }

    /**
     * Safely retrieves the tile-type for the specified position.
     *
     * This will perform checks on the position.
     *
     * @param tileX The tile X position
     * @param tileY The tile Y position
     * @return Instance, or null if invalid position
     */
    public TileType tileTypeFromPosition(int tileX, int tileY)
    {
        // Check within bounds of map
        if (tileX < 0 || tileY < 0 || tileX >= width || tileY >= height)
        {
            return null;
        }

        // Retrieve type
        return tileTypes[tiles[tileY][tileX]];
    }
    
    public static Map load(Controller controller, MapManager mapManager, short mapId, JSONObject rootJsonNode) throws IOException
    {
        Map map = new Map(controller, mapManager, mapId);
        
        // Load map tile imp
        JSONArray tileTypes = (JSONArray) rootJsonNode.get("tile_types");
        
        // Setup array for imp
        map.tileTypes = new TileType[tileTypes.size()];
        
        JSONObject arrayObj;
        TileType tileType;

        for(Object rawTileType : tileTypes)
        {
            // Parse tile type
            arrayObj = (JSONObject) rawTileType;
            tileType = TileType.load(controller, arrayObj);
            
            // Assign ID to type
            tileType.id = map.tileTypeIdCounter++;
            
            // Add mapping
            map.tileNameToTypeIndexMappings.put(tileType.name, tileType.id);
            
            // Store type
            map.tileTypes[tileType.id] = tileType;
        }
        
        // Load properties
        map.name = (String) rootJsonNode.get("name");
        map.tileSize = (short) (long) rootJsonNode.get("tile_size");
        map.width = (short) (long) rootJsonNode.get("width");
        map.height = (short) (long) rootJsonNode.get("height");
        
        // Compute max boundries
        map.maxX = (float) map.tileSize * (float) map.width;
        map.maxY = (float) map.tileSize * (float) map.height;
        
        // Setup tiles array
        map.tiles = new short[map.height][map.width];
        
        // Setup vertices array
        map.tileVertices = new Vertices[map.height][map.width];
        
        // Load tiles
        JSONArray tiles = (JSONArray) rootJsonNode.get("tiles");
        
        // Check we have a correct number of tiles
        if (tiles.size() != (map.width * map.height))
        {
            throw new IOException("Invalid tile count for map " + map.name +
                    " - expected " + (map.width * map.height) +
                    " tiles, found " + tiles.size()
            );
        }
        
        // Parse imp of tiles and build vertices
        int yoffset = 0;
        String tile;
        short typeIndex;
        TileType type;
        
        // Note: y is inverted since 0 is bottom and x is top!
        for(int y = map.height - 1; y >= 0; y--)
        {
            for(int x = 0; x < map.width; x++)
            {
                // Fetch tile
                tile = (String) tiles.get(yoffset++);
                
                // Locate actual type
                typeIndex = map.tileNameToTypeIndexMappings.get(tile);
                type = map.tileTypes[typeIndex];
                
                if(type == null)
                {
                    throw new IOException("Failed to find tile '" + tile +
                            "' [x: " + x + ", y: " + y + "] for map '" +
                            map.name + "'"
                    );
                }
                
                // Assign type
                map.tiles[y][x] = type.id;
                
                // Build vertices
                map.tileVertices[y][x] = Vertices.buildTileVertices(map, x, y);
            }
        }
        
        // Build map packet
        map.packet = new MapDataOutboundPacket();
        map.packet.build(map);
        
        // Parse factions
        JSONArray factions = (JSONArray) rootJsonNode.get("factionSpawns");
        for (Object factionData : factions)
        {
            parseFactionSpawns(controller, map, (JSONObject) factionData);
        }
        
        // Spawn ents into world
        JSONArray ents = (JSONArray) rootJsonNode.get("ents");
        for (Object entData : ents)
        {
            parseEnts(controller, mapManager, map, (JSONObject) entData);
        }
        
        return map;
    }
    
    private static void parseEnts(Controller controller, MapManager mapManager, Map map, JSONObject entData) throws IOException
    {
        // Fetch ent type - either by ID or name
        Class entClass;

        if (entData.containsKey("typeName"))
        {
            entClass = controller.entityManager.entTypeMappings.getMappingByTypeName((String) entData.get("typeName"));
        }
        else if (entData.containsKey("typeId"))
        {
            entClass = controller.entityManager.entTypeMappings.getMappingByTypeId((short) (long) entData.get("typeId"));
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
        JSONObject rawKV = (JSONObject) entData.get("kv");
        MapEntKV mapEntKV;

        if (rawKV != null)
        {
            mapEntKV = parseEntsCreateEntKVHashMap(rawKV);
        }
        else
        {
            mapEntKV = null;
        }

        // Create new instances of type
        parseEntsCreateEnts(controller, map, entClass, mapEntKV, count, faction, spawn);
    }

    private static MapEntKV parseEntsCreateEntKVHashMap(JSONObject rawKV)
    {
        MapEntKV mapEntKV = new MapEntKV();

        // Parse each KV
        Iterator iterator = rawKV.entrySet().iterator();
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

    private static void parseEntsCreateEnts(Controller controller, Map map, Class entClass, MapEntKV mapEntKV, long count, short faction, Spawn spawn) throws IOException
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
    
    private static void parseFactionSpawns(Controller controller, Map map, JSONObject factionData)
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
        controller.respawnManager.factionSpawnsAdd(map.mapId, factionSpawns);
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[\n");
        
        // Add main attributes
        sb.append("\tname:\t\t").append(name).append("\n");
        sb.append("\ttile size:\t").append(tileSize).append("\n");
        sb.append("\twidth:\t\t").append(width).append("\n");
        sb.append("\theight:\t\t").append(height).append("\n");
        sb.append("\ttile imp:\n");
        
        // Add tile imp
        short typeIndex;
        for(java.util.Map.Entry<String, Short> kv : tileNameToTypeIndexMappings.entrySet())
        {
            typeIndex = kv.getValue();
            sb.append("\t\t").append(tileTypes[typeIndex]).append("\n");
        }
        
        // Print tile map
        sb.append("\ttiles:\n\t\ty\trow data\n");
        short[] row;
        for(int y = 0; y < tiles.length; y++)
        {
            row = tiles[y];
            sb.append("\t\t").append(y).append(":\t");
            for(int x = 0; x < row.length; x++)
            {
                sb.append(row[x]).append(",");
            }
            sb.deleteCharAt(sb.length()-1).append("\n");
        }
        
        sb.append("]");
        
        return sb.toString();
    }
    
}
