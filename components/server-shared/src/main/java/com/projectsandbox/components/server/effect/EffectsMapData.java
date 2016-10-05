package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 21/07/16.
 */
@Component
@Scope(value = "prototype")
public class EffectsMapData implements MapData
{
    protected List<AbstractEffect> pendingSend;

    public EffectsMapData()
    {
        this.pendingSend = new LinkedList<>();
    }

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root)
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root)
    {
    }

}
