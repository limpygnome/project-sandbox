package com.projectsandbox.server.map.repository.file.load;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.EntityTypeMappingStoreService;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.map.MapEntKV;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import com.projectsandbox.components.server.world.spawn.FactionSpawns;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

/**
 * A generic map builder for parsing map data from JSON.
 */
public abstract class FileSystemGenericWoldMapBuilder implements FileSystemMapBuilder
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
