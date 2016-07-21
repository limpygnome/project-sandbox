package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 21/07/16.
 */
public class EffectsMapData
{
    protected List<AbstractEffect> pendingSend;

    public EffectsMapData()
    {
        this.pendingSend = new LinkedList<>();
    }

}
