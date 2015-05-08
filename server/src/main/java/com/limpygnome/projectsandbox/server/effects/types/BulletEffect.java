package com.limpygnome.projectsandbox.server.effects.types;

import java.util.List;

/**
 * Created by limpygnome on 06/05/15.
 */
public class BulletEffect extends AbstractEffect
{
    public float x;
    public float y;

    public BulletEffect(float x, float y)
    {
        super((byte) 'B');

        this.x = x;
        this.y = y;
    }

    @Override
    public void writeCustomData(List<Object> packetData)
    {
        packetData.add(x);
        packetData.add(y);
    }
}
