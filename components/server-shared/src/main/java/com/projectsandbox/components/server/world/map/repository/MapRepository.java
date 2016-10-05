package com.projectsandbox.components.server.world.map.repository;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Map;

/**
 * Used to load maps, or a single map, from a data-source.
 */
public interface MapRepository
{

    /**
     * Used to fetch all public maps.
     */
    Map<String, WorldMap> fetchPublicMaps(Controller controller, MapService mapService);

    /**
     * Persists changes for a map.
     */
    void persist(Controller controller, WorldMap map);

    /**
     * Reloads the map.
     */
    void reload(Controller controller, WorldMap map);

}
