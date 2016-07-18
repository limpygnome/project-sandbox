package com.projectsandbox.server.map.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.MapComponent;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by limpygnome on 18/07/16.
 */
@Component
public class Tiles implements MapComponent
{

    @Override
    public void load(Controller controller, JSONObject mapData, WorldMap map) throws IOException
    {
        if (map instanceof TileWorldMap)
        {
            // load tiles...
        }
    }

    @Override
    public void persist(Controller controller, WorldMap map)
    {
        if (map instanceof TileWorldMap)
        {
            // load tiles..
        }
    }

}
