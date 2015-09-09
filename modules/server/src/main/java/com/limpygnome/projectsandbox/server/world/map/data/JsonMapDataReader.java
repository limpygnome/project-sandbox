package com.limpygnome.projectsandbox.server.world.map.data;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.Map;
import com.limpygnome.projectsandbox.server.world.map.MapManager;

/**
 * Created by limpygnome on 09/09/15.
 */
public class JsonMapDataReader implements MapDataReader
{

    /**
     * Loads the map from the data.
     *
     * If the map cannot be loaded, a runtime exception is thrown.
     *
     * Thread safe.
     *
     * @param controller The controller
     * @param mapManager The map manager
     * @param data The map data
     * @return
     */
    @Override
    public Map load(Controller controller, MapManager mapManager, byte[] data)
    {
    }

}
