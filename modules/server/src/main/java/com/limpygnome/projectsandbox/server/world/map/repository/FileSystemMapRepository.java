package com.limpygnome.projectsandbox.server.world.map.repository;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.packet.imp.map.MapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;
import com.limpygnome.projectsandbox.server.world.map.WorldMapTileData;
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
    public WorldMap fetchMap(Controller controller, MapManager mapManager, MapBuilder mapBuilder, UUID uuid)
    {
        throw new RuntimeException("no support for loading individual maps");
    }

    private WorldMap buildMap(Controller controller, MapManager mapManager, JSONObject mapData) throws IOException
    {
        // Parse unique identifier...
        String rawMapId = (String) mapData.get("id");
        UUID mapId = UUID.fromString(rawMapId);

        // Create new instance
        WorldMap map = new WorldMap(controller, mapManager, mapId);

        // Build parts of map from JSON data
        buildMapProperties(map, mapData);
        buildTileTypesAndTiles(controller, mapData, map);
        buildFactionSpawns(mapData, map);
        buildEntities(mapData, map);

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
        WorldMapTileData tileData = new WorldMapTileData();

        // Load tile properties
        buildTileProperties(tileData, mapData);

        // Load tile-type data
        TileType[] tileTypes = buildTileTypes(controller, mapData);

        // Create map to speed-up 'tile-name -> ID' translation
        Map<String, TileType> tileTypeByNameMappings = buildTileTypeMap(tileTypes);

        // Load tiles
        buildTiles(map, tileTypeByNameMappings, mapData);

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
        map.tileData.tiles = new short[map.tileData.heightTiles][map.tileData.widthTiles];

        // Setup vertices array
        map.tileData.tileVertices = new Vertices[map.tileData.heightTiles][map.tileData.widthTiles];

        // Parse tiles
        int yOffset = 0;
        String tile;
        short typeIndex;
        TileType type;

        // -- Note: y is inverted since 0 is bottom and x is top!
        for(int y = map.tileData.heightTiles - 1; y >= 0; y--)
        {
            for(int x = 0; x < map.tileData.widthTiles; x++)
            {
                // Fetch tile
                tile = (String) tiles.get(yOffset++);

                // Locate actual type
                typeIndex = tileTypeMap.get(tile).id;
                type = map.tileData.tileTypes[typeIndex];

                if(type == null)
                {
                    throw new IOException("Failed to find tile '" + tile +
                            "' [x: " + x + ", y: " + y + "] for map '" +
                            map.properties.name + "'"
                    );
                }

                // Assign type
                map.tileData.tiles[y][x] = type.id;

                // Build vertices
                map.tileData.tileVertices[y][x] = Vertices.buildTileVertices(map, x, y);
            }
        }
    }

    private void buildFactionSpawns(Controller controller, JSONObject mapData, WorldMap map)
    {
        JSONArray rawSpawnData = (JSONArray) mapData.get("factionSpawns");
        JSONObject rawFactionSpawns;

        for (Object factionData : factions)
        {
            rawFactionSpawns = (JSONObject) factionData;
            parseFactionSpawn(controller, map, rawFactionSpawns);
        }
    }

    private void buildEntities(JSONObject mapData, WorldMap map)
    {
    }

}
