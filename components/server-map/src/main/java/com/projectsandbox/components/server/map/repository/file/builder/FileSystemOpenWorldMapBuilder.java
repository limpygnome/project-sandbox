package com.projectsandbox.components.server.map.repository.file.builder;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Implementation for {@link OpenWorldMap}.
 */
@Component
public class FileSystemOpenWorldMapBuilder extends FileSystemGenericWoldMapBuilder
{

    @Override
    public String getBuilderName()
    {
        return "open-world-map";
    }

    @Override
    public WorldMap createMapInstance(Controller controller, MapService mapService, short mapId)
    {
        return new OpenWorldMap(mapId);
    }

    /**
     * A generic map builder for parsing map data from JSON.
     */
    public abstract static class FileSystemGenericWoldMapBuilder implements FileSystemMapBuilder
    {
        private final static Logger LOG = LogManager.getLogger(FileSystemGenericWoldMapBuilder.class);

        /* Fetch map components to parse and load map. */
        @Autowired
        private List<MapComponent> mapComponents;

        @Override
        public WorldMap build(Controller controller, MapService mapService, JSONObject mapData) throws IOException
        {
            // Parse unique identifier...
            short mapId = (short) (long) mapData.get("id");

            // Create new instance
            WorldMap map = createMapInstance(controller, mapService, mapId);

            if (map == null)
            {
                throw new RuntimeException("Implementation did not create map instance...");
            }

            // Use components to load rest of map
            for (MapComponent component : mapComponents)
            {
                component.load(controller, mapData, map);
            }

            // Check map is enabled...
            if (!map.getProperties().isEnabled())
            {
                LOG.warn("Map not enabled, skipped loading - id: {}", mapId);
                return null;
            }

            return map;
        }


    }
}
