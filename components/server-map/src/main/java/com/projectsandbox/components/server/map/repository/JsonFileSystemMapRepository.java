package com.projectsandbox.components.server.map.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.util.JsonHelper;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.repository.MapRepository;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Used to load maps from a file-system or the class-path.
 */
@Repository
public class JsonFileSystemMapRepository implements MapRepository
{
    private final static Logger LOG = LogManager.getLogger(JsonFileSystemMapRepository.class);

    @Autowired
    private JsonHelper jsonHelper;

    @Value("${maps.public.location}")
    private String publicMapsLocationPath;

    @Override
    public Map<String, WorldMap> fetchPublicMaps(Controller controller, MapService mapService)
    {
        Map<String, WorldMap> maps = new HashMap<>();

        try
        {
            Reflections reflections = new Reflections("data.maps.main.", new ResourcesScanner());
            Set<String> resources = reflections.getResources(Pattern.compile("(.+)\\.json"));

            // Iterate and load each map file
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

                LOG.debug("loaded public map - {}", map);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("failed to load common maps", e);
        }

        return maps;
    }

    private WorldMap readMap(Controller controller, JSONObject root) throws IOException
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

        // Create map based on type
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

        // Deserialize map data (i.e. let each map data load its own data from the root JSON element)
        List<MapData> mapDataList = map.getMapData();
        for (MapData mapData : mapDataList)
        {
            mapData.deserialize(controller, map, root);
        }

        return map;
    }

    @Override
    public void persist(Controller controller, WorldMap map)
    {
        try
        {
            // Determine base path of maps directory
            String basePath = getClass().getResource("/").getFile() + publicMapsLocationPath;

            // Build path for file and check we can write to parent
            File path = new File(basePath + "/" + map.getMapId() + ".json");

            if (!path.getParentFile().exists())
            {
                throw new RuntimeException("Unable to persist map, parent path does not exist: " + path.getAbsolutePath());
            }

            LOG.info("Writing map to file - id: {}, path: {}", map.getMapId(), path);

            // Create root for attaching map data
            JSONObject root = new JSONObject();

            // Invoke serialize on all map data instances
            for (MapData mapData : map.getMapData())
            {
                mapData.serialize(controller, map, root);
            }

            // Serialize json
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(root);

            // Persist to file system
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(data);
            writer.flush();
            writer.close();

            LOG.info("Successfully persisted map - id: {}, path: {}", map.getMapId(), path.getAbsolutePath());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to persist map", e);
        }
    }

    @Override
    public void reload(Controller controller, WorldMap map)
    {
    }

}
