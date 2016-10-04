package com.projectsandbox.components.server.entity.respawn;

import com.projectsandbox.components.server.world.spawn.Spawn;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created by limpygnome on 18/07/16.
 */
@Component
public class SpawnParserHelper
{

    public Spawn parseSpawn(JSONObject spawn)
    {
        if (spawn != null)
        {
            /*
                Parse each component, but Z is optional.
             */

            float x = (float) (double) spawn.get("x");
            float y = (float) (double) spawn.get("y");
            float z = (float) (double) (spawn.containsKey("z") ? spawn.get("z") : 0.0);
            float rotation = (float) (double) spawn.get("rotation");

            return new Spawn(x, y, z, rotation);
        }
        else
        {
            return null;
        }
    }

}
