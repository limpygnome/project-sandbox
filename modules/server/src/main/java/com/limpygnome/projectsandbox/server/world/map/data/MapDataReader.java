package com.limpygnome.projectsandbox.server.world.map.data;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.Map;
import com.limpygnome.projectsandbox.server.world.map.MapManager;

/**
 * Created by limpygnome on 09/09/15.
 */
public interface MapDataReader
{

    /**
     * Loads a map from data.
     *
     * @param controller The controller
     * @param mapManager The map manager
     * @param data The map data
     * @return An instance of map
     */
    Map load(Controller controller, MapManager mapManager, byte[] data);

}
