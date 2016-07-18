package com.projectsandbox.server.map.repository.file;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.util.JsonHelper;
import com.projectsandbox.components.server.world.map.repository.MapRepository;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.server.map.repository.file.load.FileSystemMapBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JsonHelper jsonHelper;

    @Override
    public Map<Short, WorldMap> fetchPublicMaps(Controller controller, MapService mapService)
    {
        Map<Short, WorldMap> maps = new HashMap<>();

        try
        {
            Reflections reflections = new Reflections("data.map", new ResourcesScanner());
            Set<String> resources = reflections.getResources(Pattern.compile("(.+)\\.json"));

            // Iterate and load each map file
            String mapType;
            FileSystemMapBuilder mapBuilder;
            JSONObject mapData;
            WorldMap map;
            InputStream inputStream;

            for (String resource : resources)
            {
                // Load map data
                try
                {
                    inputStream = getClass().getClassLoader().getResourceAsStream(resource);
                    mapData = jsonHelper.read(inputStream);
                }
                catch (Exception e)
                {
                    mapData = null;
                }

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
