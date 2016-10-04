package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.world.map.MapData;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 21/07/16.
 */
public class EffectsMapData implements MapData
{
    private static final long serialVersionUID = 1L;

    protected List<AbstractEffect> pendingSend;

    public EffectsMapData()
    {
        this.pendingSend = new LinkedList<>();
    }

    @Override
    public void serialize(JSONObject root)
    {
    }

    @Override
    public void deserialize(JSONObject root)
    {
    }

}
