package com.limpygnome.projectsandbox.server.world.map.repository.file;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.constant.PathConstants;
import com.limpygnome.projectsandbox.server.util.FileSystem;
import com.limpygnome.projectsandbox.server.util.FileSystemFile;
import com.limpygnome.projectsandbox.server.util.JsonHelper;
import com.limpygnome.projectsandbox.server.world.map.*;
import com.limpygnome.projectsandbox.server.world.map.repository.MapRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Used to load maps from a file-system or the class-path.
 */
@Repository
public class FileSystemMapRepository implements MapRepository
{
    private final static Logger LOG = LogManager.getLogger(FileSystemMapRepository.class);

    @Resource(name = "fileSystemMapBuilders")
    private Map<String, FileSystemMapBuilder> builders;

    @Override
    public Map<Short, WorldMap> fetchPublicMaps(Controller controller, MapService mapService)
    {
        Map<Short, WorldMap> maps = new HashMap<>();

        try
        {
            FileSystemFile[] files = FileSystem.getResources(PathConstants.BASE_PACKAGE_MAPS);

            // Iterate and load each map file
            String mapType;
            FileSystemMapBuilder mapBuilder;
            JSONObject mapData;
            WorldMap map;

            for (FileSystemFile file : files)
            {
                // Load map data
                mapData = JsonHelper.read(file.getInputStream());

                // Read type and fetch builder
                mapType = (String) mapData.get("type");
                mapBuilder = builders.get(mapType);

                if (mapBuilder == null)
                {
                    throw new RuntimeException("Unable to find map builder for map type: " + mapType);
                }

                // Build map using data
                map = mapBuilder.build(controller, mapService, mapData);

                if (map != null)
                {
                    // Prepare for runtime
                    map.postMapLoad();

                    // Build initial packet
                    map.rebuildMapPacket();

                    // Add to result
                    maps.put(map.getMapId(), map);

                    LOG.debug("loaded public map - {}", map);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to load common maps", e);
        }

        return maps;
    }

    @Override
    public WorldMap fetchMap(Controller controller, MapService mapService, UUID uuid)
    {
        throw new RuntimeException("no support for loading individual maps");
    }

}
