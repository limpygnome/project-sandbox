package com.limpygnome.projectsandbox.world;

import com.limpygnome.projectsandbox.ents.physics.Vertices;
import com.limpygnome.projectsandbox.packets.outbound.MapDataPacket;
import java.io.IOException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class Map
{
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
    
    /** Mapped by [y][x] or [row][column] */
    public short tiles[][];
    public Vertices[][] tileVertices;
    
    // if this is updated, it needs thread protection
    public MapDataPacket packet;
    
    public Map(MapManager mapManager)
    {
        this.mapManager = mapManager;
        this.tileTypeMappings = new HashMap<>();
        this.tileTypeIdCounter = 0;
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
        map.packet = new MapDataPacket();
        map.packet.build(map);
        
        return map;
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
