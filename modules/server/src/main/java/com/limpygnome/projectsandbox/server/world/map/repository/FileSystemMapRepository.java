package com.limpygnome.projectsandbox.server.world.map.repository;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;
import com.limpygnome.projectsandbox.server.world.map.data.MapBuilder;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to load maps from a file-system or the class-path.
 */
public class FileSystemMapRepository implements MapRepository
{
    private final static Logger LOG = LogManager.getLogger(FileSystemMapRepository.class);

    @Override
    public Map<UUID, WorldMap> fetchPublicMaps(Controller controller, MapManager mapManager, MapBuilder mapBuilder)
    {
        Map<UUID, WorldMap> maps = new HashMap<>();

        try
        {
            FileSystemFile[] files = FileSystem.getResources(PathConstants.BASE_PACKAGE_MAPS);

            // Iterate and load each map file
            JSONObject mapData;
            String rawMapId;
            UUID mapId;
            WorldMap map;

            for (FileSystemFile file : files)
            {
                // Load map data
                mapData = JsonHelper.read(file.getInputStream());

                // Create new map
                rawMapId = (String) mapData.get("id");
                mapId = UUID.fromString(rawMapId);
                map = new WorldMap(controller, mapManager, mapId);

                // Build parts of map from JSON data
                buildMapProperties(map, mapData);
                buildTileTypesAndTiles(controller, mapData, mapBuilder, map);
                buildEntities(mapData, mapBuilder, map);
                buildSpawns(mapData, mapBuilder, map);

                // Add to result
                maps.put(mapId, map);

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
    public WorldMap fetchMap(Controller controller, MapManager mapManager, MapBuilder mapBuilder, UUID uuid)
    {
        throw new RuntimeException("no support for loading individual maps");
    }

    private void buildMapProperties(WorldMap map, JSONObject mapData)
    {
        WorldMapProperties properties = new WorldMapProperties();

        // Fetch properties node from map data
        JSONObject rawProperties = (JSONObject) mapData.get("properties");

        properties.name = (String) rawProperties.get("name");
        properties.tileSize = (float) (long) rawProperties.get("tile_size");
        properties.tileSizeHalf = properties.tileSize / 2.0f;
        properties.tileSizeQuarter = properties.tileSize / 4.0f;
        properties.tilesWidth = (short) (long) rawProperties.get("tiles_width");
        properties.tilesHeight = (short) (long) rawProperties.get("tiles_height");

        // Compute max boundaries
        properties.maxX = map.tileSize * (float) map.width;
        properties.maxY = map.tileSize * (float) map.height;

        // Set map with properties loaded
        map.properties = properties;
    }

    private void buildTileTypesAndTiles(Controller controller, JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
            throws IOException
    {
        // Load tile-type data
        TileType[] tileTypes = buildTileTypes(controller, mapData);

        // Create map to speed-up tile-name -> ID translation
        Map<String, TileType> tileTypeByNameMappings = buildTileTypeMap(tileTypes);

        // Load tiles
        buildTiles(map, tileTypeByNameMappings, mapData);

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

        for(Object rawTileType : tileTypes)
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

    private void buildTiles(WorldMap map, Map<String, TileType> tileTypeMap, JSONObject mapData) throws IOException
    {
        JSONArray tiles = (JSONArray) mapData.get("tiles");

        // Setup tiles array
        map.tiles = new short[map.height][map.width];

        // Setup vertices array
        map.tileVertices = new Vertices[map.height][map.width];

        // Parse tiles
        int yOffset = 0;
        String tile;
        short typeIndex;
        TileType type;

        // -- Note: y is inverted since 0 is bottom and x is top!
        for(int y = map.height - 1; y >= 0; y--)
        {
            for(int x = 0; x < map.width; x++)
            {
                // Fetch tile
                tile = (String) tiles.get(yOffset++);

                // Locate actual type
                typeIndex = tileTypeMap.get(tile).id;
                type = map.tileTypes[typeIndex];

                if(type == null)
                {
                    throw new IOException("Failed to find tile '" + tile +
                            "' [x: " + x + ", y: " + y + "] for map '" +
                            map.properties.name + "'"
                    );
                }

                // Assign type
                map.tiles[y][x] = type.id;

                // Build vertices
                map.tileVertices[y][x] = Vertices.buildTileVertices(map, x, y);
            }
        }
    }

    private void buildEntities(JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
    {
    }

    private void buildSpawns(JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
    {
    }

}
