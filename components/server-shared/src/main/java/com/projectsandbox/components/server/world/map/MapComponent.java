package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Created by limpygnome on 18/07/16.
 */
public interface MapComponent
{

    void load(Controller controller, JSONObject mapData, WorldMap map) throws IOException;

    void persist(Controller controller, JSONObject rootObject, WorldMap map);

}
