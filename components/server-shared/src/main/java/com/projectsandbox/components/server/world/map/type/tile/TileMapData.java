package com.projectsandbox.components.server.world.map.type.tile;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.MapPosition;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to hold tile data for an instance of {@link WorldMap}.
 */
public class TileMapData implements MapData
{
    private WorldMap map;

    public TileType[] tileTypes;

    public float tileSize;
    public float tileSizeHalf;
    public float tileSizeQuarter;

    public short widthTiles;
    public short heightTiles;
    public float maxX;
    public float maxY;

    /** Mapped by [y][x] or [row][column]; bottom-left at 0, top-right at n */
    public short tiles[][];

    public Vertices[][] tileVertices;

    /**
     * Creates a new instance.
     *
     * @param map the map to which this belongs
     */
    public TileMapData(WorldMap map)
    {
        this.map = map;
    }

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root) throws IOException
    {
        buildTileTypesAndTiles(controller, root, (TileWorldMap) map);
    }

    private void buildTileTypesAndTiles(Controller controller, JSONObject root, TileWorldMap map)
            throws IOException
    {
        TileMapData tileMapData = new TileMapData(map);

        // Load tile properties
        buildTileProperties(tileMapData, root);

        // Load tile-type data
        TileType[] tileTypes = buildTileTypes(controller, root);
        tileMapData.tileTypes = tileTypes;

        // Create map to speed-up 'tile-name -> ID' translation
        Map<String, TileType> tileTypeByNameMappings = buildTileTypeMap(tileTypes);

        // Load tiles
        buildTiles(map, tileTypeByNameMappings, root, tileMapData);

        // Set map tile data
        map.tileMapData = tileMapData;
    }

    private void buildTileProperties(TileMapData tileMapData, JSONObject root)
    {
        JSONObject rawTileProperties = (JSONObject) root.get("tileProperties");

        // Parse tile properties
        tileMapData.tileSize = (float) (long) rawTileProperties.get("tileSize");
        tileMapData.tileSizeHalf = tileMapData.tileSize / 2.0f;
        tileMapData.tileSizeQuarter = tileMapData.tileSize / 4.0f;
        tileMapData.widthTiles = (short) (long) rawTileProperties.get("tilesWidth");
        tileMapData.heightTiles = (short) (long) rawTileProperties.get("tilesHeight");

        // Compute max boundaries
        tileMapData.maxX = tileMapData.tileSize * (float) tileMapData.widthTiles;
        tileMapData.maxY = tileMapData.tileSize * (float) tileMapData.heightTiles;
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

    private void buildTiles(WorldMap map, Map<String, TileType> tileTypeMap, JSONObject mapData, TileMapData tileMapData) throws IOException
    {
        JSONArray tiles = (JSONArray) mapData.get("tiles");

        // Setup tiles array
        tileMapData.tiles = new short[tileMapData.heightTiles][tileMapData.widthTiles];

        // Setup vertices array
        tileMapData.tileVertices = new Vertices[tileMapData.heightTiles][tileMapData.widthTiles];

        // Parse tiles
        int yOffset = 0;
        String tile;
        short typeIndex;
        TileType type;

        // -- Note: y is inverted since 0 is bottom and x is top!
        for(int y = tileMapData.heightTiles - 1; y >= 0; y--)
        {
            for(int x = 0; x < tileMapData.widthTiles; x++)
            {
                // Fetch tile
                tile = (String) tiles.get(yOffset++);

                // Locate actual type
                typeIndex = tileTypeMap.get(tile).id;
                type = tileMapData.tileTypes[typeIndex];

                if(type == null)
                {
                    throw new IOException("Failed to find tile '" + tile +
                            "' [x: " + x + ", y: " + y + "] for map '" +
                            map.getMapId() + "'"
                    );
                }

                // Assign type
                tileMapData.tiles[y][x] = type.id;

                // Build vertices
                tileMapData.tileVertices[y][x] = Vertices.buildTileVertices(tileMapData, x, y);
            }
        }
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
    public synchronized TileType tileTypeFromPosition(int tileX, int tileY)
    {
        // Check within bounds of map
        if (tileX < 0 || tileY < 0 || tileX >= widthTiles || tileY >= heightTiles)
        {
            return null;
        }

        // Retrieve tile-type at co-ordinate
        short tileTypeId = tiles[tileY][tileX];

        // Return the type, since tile ID is the same as array index
        return tileTypes[tileTypeId];
    }

    /**
     * Determines the tile position from a vector.
     *
     * @param vector the vector of x,y
     * @return the result
     */
    public MapPosition positionFromReal(Vector2 vector)
    {
        return positionFromReal(vector.x, vector.y);
    }

    /**
     * Determines the position from 2D co-ordinates.
     *
     * @param x the x position
     * @param y the y position
     * @return the result
     */
    public MapPosition positionFromReal(float x, float y)
    {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        return new MapPosition(
                map,
                x,
                y,
                tileX,
                tileY
        );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("tileMapData{");

        // Append tile properties
        sb.append("tile size: ").append(tileSize).append(",");
        sb.append("tile size half: ").append(tileSizeHalf).append(",");
        sb.append("tile size quart: ").append(tileSizeQuarter).append(",");
        sb.append("width tiles: ").append(widthTiles).append(",");
        sb.append("height tiles: ").append(heightTiles).append(",");
        sb.append("max x: ").append(maxX).append(",");
        sb.append("max y: ").append(maxY).append(",");

        // Append tile types
        sb.append("tileTypes{");
        for (TileType tileType : tileTypes)
        {
            sb.append(tileType);
        }
        sb.append("}");

        return sb.toString();
    }

}
