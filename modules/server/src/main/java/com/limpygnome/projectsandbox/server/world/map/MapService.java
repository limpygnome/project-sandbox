package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.Controller;

import com.limpygnome.projectsandbox.server.service.EventServerPreStartup;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import java.util.HashMap;
import java.util.Map;

import com.limpygnome.projectsandbox.server.world.map.repository.MapRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Responsible for managing/handling maps.
 */
@Service
public class MapService implements EventServerPreStartup, EventLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(MapService.class);

    @Autowired
    private Controller controller;

    // The repository used for fetching maps
    @Autowired
    private MapRepository mapRepository;

    // A cache for storing either common or active maps
    private Map<Short, WorldMap> mapCache;

    // The mapMain/lobby map
    public WorldMap mainMap;

    public MapService()
    {
        this.mapCache = new HashMap<>();
        this.mainMap = null;
    }

    public synchronized void put(WorldMap map)
    {
        mapCache.put(map.getMapId(), map);
    }

    @Override
    public synchronized void eventServerPreStartup(Controller controller)
    {
        // Load public maps into cache
        Map<Short, WorldMap> publicMaps = mapRepository.fetchPublicMaps(controller, this);
        mapCache = new HashMap<>(publicMaps);

        // Set the mapMain/lobby map
        this.mainMap = null;

        WorldMap map;
        for (Map.Entry<Short, WorldMap> kv : mapCache.entrySet())
        {
            map = kv.getValue();

            if (map.getProperties().isLobby())
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

        LOG.info("Loaded {} maps, lobby: {} [uuid: {}]", mapCache.size(), mainMap.getProperties().getName(), mainMap.getMapId());
    }

    @Override
    public void logic()
    {
        // Execute logic for each map...
        WorldMap map;

        for (Map.Entry<Short, WorldMap> kv : mapCache.entrySet())
        {
            map = kv.getValue();
            map.logic();
        }
    }

}
