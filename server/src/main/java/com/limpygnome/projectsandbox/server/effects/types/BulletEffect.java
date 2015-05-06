package com.limpygnome.projectsandbox.server.effects.types;

import com.limpygnome.projectsandbox.server.effects.AbstractEffect;

/**
 * Created by limpygnome on 06/05/15.
 */
public class BulletEffect extends AbstractEffect
{
    public BulletEffect(float x, float y)
    {
        super((byte) 'B', x, y);
    }
}
