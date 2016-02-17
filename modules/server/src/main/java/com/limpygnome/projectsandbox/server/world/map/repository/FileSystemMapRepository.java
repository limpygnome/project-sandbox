package com.limpygnome.projectsandbox.server.world.map.repository;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.data.MapBuilder;
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
                buildTiles(mapData, mapBuilder, map);
                buildEntities(mapData, mapBuilder, map);
                buildSpawns(mapData, mapBuilder, map);

                // Add to result
                maps.put(mapId, map);

                LOG.debug("loaded public map - {}", map);
            }

            // Set the main map file
            main = mapCache.get("main");

            if (main == null)
            {
                throw new IOException("Unable to find main map.");
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

    private void buildTiles(JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
    {
    }

    private void buildEntities(JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
    {
    }

    private void buildSpawns(JSONObject mapData, MapBuilder mapBuilder, WorldMap map)
    {
    }

}
