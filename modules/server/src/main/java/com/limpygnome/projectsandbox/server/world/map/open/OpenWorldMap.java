package com.limpygnome.projectsandbox.server.world.map.open;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.open.OpenWorldMapProperties;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    private OpenWorldMapProperties properties;

    public OpenWorldMap(Controller controller, MapService mapService, short mapId)
    {
        super(controller, mapService, mapId);
    }

    @Override
    public OpenWorldMapProperties getProperties()
    {
        return properties;
    }

}
