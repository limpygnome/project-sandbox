package com.projectsandbox.components.server.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 21/07/16.
 */
public class EffectsMapData implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected List<AbstractEffect> pendingSend;

    public EffectsMapData()
    {
        this.pendingSend = new LinkedList<>();
    }

}
