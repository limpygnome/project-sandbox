package com.projectsandbox.components.server.world.map.repository;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Map;
import java.util.UUID;

/**
 * Used to load maps, or a single map, from a data-source.
 */
public interface MapRepository
{

    /**
     * Used to fetch all public maps.
     *
     * @return
     */
    Map<String, WorldMap> fetchPublicMaps(Controller controller, MapService mapService);

    void persist(WorldMap map);

}
