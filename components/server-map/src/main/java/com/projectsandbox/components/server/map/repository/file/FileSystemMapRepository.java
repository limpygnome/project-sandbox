package com.projectsandbox.components.server.map.repository.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.map.repository.file.builder.FileSystemMapBuilder;
import com.projectsandbox.components.server.util.JsonHelper;
import com.projectsandbox.components.server.world.map.repository.MapRepository;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import jdk.nashorn.internal.objects.NativeArray;
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

    @Autowired
    private JsonHelper jsonHelper;

    @Override
    public Map<String, WorldMap> fetchPublicMaps(Controller controller, MapService mapService)
    {
        Map<String, WorldMap> maps = new HashMap<>();

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

                // Read map
                map = readMap(controller, mapData);

                // Prepare for runtime
                map.postMapLoad();

                // Build initial packet
                map.rebuildMapPacket();

                // Add to result
                maps.put(map.getMapId(), map);

                persist(map);

                LOG.debug("loaded public map - {}", map);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to load common maps", e);
        }

        return maps;
    }

    private WorldMap readMap(Controller controller, JSONObject root)
    {
        // Parse unique identifier...
        String mapId = (String) root.get("id");

        if (mapId == null || mapId.length() == 0)
        {
            throw new RuntimeException("MapID is mandatory and missing");
        }

        // Parse type and create instance based on it
        String type = (String) root.get("type");

        if (type == null || type.length() == 0)
        {
            throw new RuntimeException("Type is mandatory and missing");
        }

        // Create new map instance
        WorldMap map;

        switch (type)
        {
            case "open-world-map":
                map = new OpenWorldMap(mapId, controller);
                break;
            case "tile-world-map":
                map = new TileWorldMap(mapId, controller);
                break;
            default:
                throw new RuntimeException("Unknown map type: " + type);
        }

        // Deserialize map data
        finish this...

        return map;
    }

    @Override
    public void persist(WorldMap map)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            //Map<String, String> test = new HashMap<>();
            //test.put("a", "123");
            //test.put("b", "456");

            String testData = mapper.writeValueAsString(map);
            LOG.error(testData);

            String mapData = mapper.writeValueAsString(map);
            String doNothing = mapData.toUpperCase();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to persist map", e);
        }
    }

}
