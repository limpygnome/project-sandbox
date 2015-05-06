package com.limpygnome.projectsandbox.server.effects;

import java.util.List;

/**
 * Created by limpygnome on 06/05/15.
 */
public class AbstractEffect
{
    public byte type;
    public float x;
    public float y;

    public AbstractEffect(byte type, float x, float y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void writePacketData(List<Object> packetData)
    {
        packetData.add(type);
        packetData.add(x);
        packetData.add(y);
    }
}
