package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.service.EventLogicCycleService;
import com.projectsandbox.components.server.service.EventMapLogicCycleService;
import com.projectsandbox.components.server.service.EventServerPreStartup;
import com.projectsandbox.components.server.world.map.repository.MapRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for managing/handling maps.
 */
@Service
public class MapService implements EventServerPreStartup, EventLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(MapService.class);

    @Autowired
    private Controller controller;

    @Autowired
    private List<EventMapLogicCycleService> eventMapLogicCycleServices;

    // The repository used for fetching maps
    @Autowired
    private MapRepository mapRepository;

    // A cache for storing either common or active maps
    private Map<String, WorldMap> mapCache;

    // The mapMain/lobby map
    public WorldMap mainMap;

    public MapService()
    {
        this.mapCache = new ConcurrentHashMap<>();
        this.mainMap = null;
    }

    public synchronized void put(WorldMap map)
    {
        mapCache.put(map.getMapId(), map);
    }

    public synchronized WorldMap get(short mapId)
    {
        return mapCache.get(mapId);
    }

    @Override
    public synchronized void eventServerPreStartup(Controller controller)
    {
        // Load public maps into cache
        Map<String, WorldMap> publicMaps = mapRepository.fetchPublicMaps(controller, this);
        mapCache = new HashMap<>(publicMaps);

        // Set the mapMain/lobby map
        this.mainMap = null;

        WorldMap map;
        for (Map.Entry<String, WorldMap> kv : mapCache.entrySet())
        {
            map = kv.getValue();

            if (map.getGeneralMapData().isLobby())
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

        LOG.info("Loaded {} maps, lobby: {} [uuid: {}]", mapCache.size(), mainMap.getGeneralMapData().getName(), mainMap.getMapId());
    }

    @Override
    public void logic()
    {
        // Execute logic for each map...
        for (WorldMap map : mapCache.values())
        {
            for (EventMapLogicCycleService service : eventMapLogicCycleServices)
            {
                service.logic(map);
            }
        }
    }

}
