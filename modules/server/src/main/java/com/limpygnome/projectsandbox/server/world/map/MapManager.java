package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.limpygnome.projectsandbox.server.world.map.data.MapBuilder;
import com.limpygnome.projectsandbox.server.world.map.repository.FileSystemMapRepository;
import com.limpygnome.projectsandbox.server.world.map.repository.MapRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for managing/handling maps.
 */
public class MapManager
{
    private final static Logger LOG = LogManager.getLogger(MapManager.class);

    private Controller controller;

    /* The repository used for fetching maps. */
    private MapRepository mapRepository;

    /* The implementation used for building maps.  */
    private MapBuilder mapBuilder;

    /* A cache for storing either common or active maps. */
    private Map<UUID, WorldMap> mapCache;

    /* The mapMain/lobby map. */
    public WorldMap mainMap;
    
    public MapManager(Controller controller)
    {
        this.controller = controller;
        this.mapCache = new HashMap<>();
        this.mainMap = null;
    }

    public synchronized void put(WorldMap map)
    {
        mapCache.put(map.mapId, map);
    }
    
    public synchronized void load(Controller controller) throws Exception
    {
        // Load public maps into cache
        MapRepository mapRepository = new FileSystemMapRepository();
        mapCache = new HashMap<>(mapRepository.fetchPublicMaps(controller, this, mapBuilder));

        // Set the mapMain/lobby map
        this.mainMap = null;

        WorldMap map;
        for (Map.Entry<UUID, WorldMap> kv : mapCache.entrySet())
        {
            map = kv.getValue();

            if (map.properties.lobby)
            {
                // Check lobby not already found; can only be one...
                if (this.mainMap != null)
                {
                    throw new RuntimeException("Only one lobby can be present");
                }

                this.mainMap = map;
            }
        }

        // Check we found main/lobby map
        if (this.mainMap == null)
        {
            throw new RuntimeException("Main/lobby map not found");
        }

        LOG.info("Loaded {} maps, lobby: {} [uuid: {}]", mapCache.size(), mainMap.properties.name, mainMap.mapId);
    }
    
}
