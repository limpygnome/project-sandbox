package com.limpygnome.projectsandbox.server.world.map.repository;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapManager;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.data.MapBuilder;

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
     * @param mapBuilder The reader used to
     * @return
     */
    Map<Short, WorldMap> fetchPublicMaps(Controller controller, MapManager mapManager, MapBuilder mapBuilder);

    /**
     * Used to fetch a map, using its identifier.
     *
     * @param mapBuilder
     * @param uuid
     * @return
     */
    WorldMap fetchMap(Controller controller, MapManager mapManager, MapBuilder mapBuilder, UUID uuid);

}
