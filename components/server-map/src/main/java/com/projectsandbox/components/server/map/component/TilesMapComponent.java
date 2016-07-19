package com.projectsandbox.components.server.map.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.map.type.tile.TileData;
import com.projectsandbox.components.server.world.map.type.tile.TileType;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by limpygnome on 18/07/16.
 */
@Component
public class TilesMapComponent implements MapComponent
{

    @Override
    public void load(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        if (map instanceof TileWorldMap)
        {
            buildTileTypesAndTiles(controller, mapData, (TileWorldMap) map);
        }
    }

    @Override
    public void persist(Controller controller, WorldMap map)
    {
        if (map instanceof TileWorldMap)
        {
            // load tiles..
        }
    }

    private void buildTileTypesAndTiles(Controller controller, JSONObject mapData, TileWorldMap map)
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
                            map.getProperties().getName() + "'"
                    );
                }

                // Assign type
                tileData.tiles[y][x] = type.id;

                // Build vertices
                tileData.tileVertices[y][x] = Vertices.buildTileVertices(tileData, x, y);
            }
        }
    }

}
