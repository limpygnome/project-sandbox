package com.limpygnome.projectsandbox.server.world;

import com.limpygnome.projectsandbox.server.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.Vertices;
import com.limpygnome.projectsandbox.server.packets.types.map.MapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class Map
{
    private final static Logger LOG = LogManager.getLogger(Map.class);

    // auto-gen by map manager
    public short id;
    
    // the types of tiles - name <> type
    public HashMap<String, Short> tileTypeMappings;
    public TileType[] tileTypes;
    private short tileTypeIdCounter;
    
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
    
    // if this is updated, it needs thread protection
    public MapDataOutboundPacket packet;
    
    private HashMap<Short, Faction> factions;
    
    public Map(MapManager mapManager)
    {
        this.mapManager = mapManager;
        this.tileTypeMappings = new HashMap<>();
        this.tileTypeIdCounter = 0;
        this.factions = new HashMap<>();
    }
    
    public static Map load(MapManager mapManager, JSONObject obj) throws IOException
    {
        Map map = new Map(mapManager);
        
        // Load map tile types
        JSONArray tileTypes = (JSONArray) obj.get("tile_types");
        
        // Setup array for types
        map.tileTypes = new TileType[tileTypes.size()];
        
        JSONObject arrayObj;
        TileType tileType;
        for(Object rawTileType : tileTypes)
        {
            // Parse tile type
            arrayObj = (JSONObject) rawTileType;
            tileType = TileType.load(mapManager.controller, arrayObj);
            
            // Assign ID to type
            tileType.id = map.tileTypeIdCounter++;
            
            // Add mapping
            map.tileTypeMappings.put(tileType.name, tileType.id);
            
            // Store type
            map.tileTypes[tileType.id] = tileType;
        }
        
        // Load properties
        map.name = (String) obj.get("name");
        map.tileSize = (short) (long) obj.get("tile_size");
        map.width = (short) (long) obj.get("width");
        map.height = (short) (long) obj.get("height");
        
        // Compute max boundries
        map.maxX = (float) map.tileSize * (float) map.width;
        map.maxY = (float) map.tileSize * (float) map.height;
        
        // Setup tiles array
        map.tiles = new short[map.height][map.width];
        
        // Setup vertices array
        map.tileVertices = new Vertices[map.height][map.width];
        
        // Load tiles
        JSONArray tiles = (JSONArray) obj.get("tiles");
        
        // Check we have a correct number of tiles
        if(tiles.size() != (map.width * map.height))
        {
            throw new IOException("Invalid tile count for map " + map.name +
                    " - expected " + (map.width * map.height) +
                    " tiles, found " + tiles.size()
            );
        }
        
        // Parse types of tiles and build vertices
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
                typeIndex = map.tileTypeMappings.get(tile);
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
        JSONArray factions = (JSONArray) obj.get("factions");
        for (Object factionData : factions)
        {
            parseFaction(map, (JSONObject) factionData);
        }
        
        // Spawn ents into world
        JSONArray ents = (JSONArray) obj.get("ents");
        for (Object entData : ents)
        {
            parseEnts(mapManager, map, (JSONObject) entData);
        }
        
        return map;
    }
    
    private static void parseEnts(MapManager mapManager, Map map, JSONObject entData) throws IOException
    {
        short type = (short) (long) entData.get("type");
            
        // Fetch class for ent type
        Class entClass = mapManager.entTypeMappings.get(type);

        // Check class was found
        if (entClass == null)
        {
            throw new IOException("Entity type " + type + " not found");
        }

        // Parse faction
        short faction = (short) (long) entData.get("faction");

        // Parse spawn (optional)
        Spawn spawn = parseSpawn((JSONObject) entData.get("spawn"));

        // Parse count to spawn / instances to create
        long count = (long) entData.get("count");
        
        // Create new instances of type
        Entity ent;
        
        for (long i = 0; i < count ;i++)
        {
            // Create instance
            try
            {
                ent = (Entity) entClass.newInstance();
                ent.faction = faction;
                ent.spawn = spawn;
            }
            catch (Exception e)
            {
                throw new IOException("Unable to create entity instance", e);
            }
            
            // Add to world
            mapManager.controller.entityManager.add(ent);
            
            // Spawn
            map.spawn(ent);
        }
    }
    
    private static void parseFaction(Map map, JSONObject factionData)
    {
        short factionId = (short) (long) factionData.get("id");
        
        Faction faction = new Faction(factionId);
        
        // Parse spawns
        JSONArray spawnsData = (JSONArray) factionData.get("spawns");
        if (spawnsData != null)
        {
            Spawn spawn;
            for (Object spawnData : spawnsData)
            {
                spawn = parseSpawn((JSONObject) spawnData);
                faction.addSpawn(spawn);
            }
        }
        
        // Add to map
        map.factions.put(faction.getFactionId(), faction);

        LOG.debug("Added faction - {}", faction);
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
    
    /**
     * Spawns an entity at the next available spawn for their faction.
     * 
     * @param <T>
     * @param ent 
     */
    public <T extends Entity> void spawn(T ent)
    {
        // Fetch spawn for faction
        Faction faction = factions.get(ent.faction);

        if (ent.spawn != null)
        {
            spawnEnt(ent, ent.spawn);
        }
        else if (faction == null)
        {
            LOG.warn("Cannot find faction for entity - id: {}, faction: {}", ent.id, ent.faction);
            ent.setState(StateChange.PENDING_DELETED);
        }
        else if (faction.hasSpawns())
        {   
            Spawn spawn = faction.getNextSpawn();
            spawnEnt(ent, spawn);
        }
        else
        {
            LOG.warn("No spawns available for faction - id: {}, faction: {}", ent.id, ent.faction);
            ent.setState(StateChange.PENDING_DELETED);
        }
    }

    private void spawnEnt(Entity ent, Spawn spawn)
    {
        // Setup entity for its new life
        ent.reset();

        // Set position etc for spawn
        ent.positionNew.x = spawn.x;
        ent.positionNew.y = spawn.y;
        ent.position.copy(ent.positionNew);
        ent.rotation = spawn.rotation;
        ent.updateMask(UpdateMasks.ALL_MASKS);

        // Rebuild vertices
        ent.rebuildCachedVertices();

        // Inform the ent it has been spawned
        ent.eventSpawn();

        LOG.debug("Spawned entity - ent: {} - spawn: {}", ent, spawn);
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
        sb.append("\ttile types:\n");
        
        // Add tile types
        short typeIndex;
        for(java.util.Map.Entry<String, Short> kv : tileTypeMappings.entrySet())
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
