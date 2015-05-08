package com.limpygnome.projectsandbox.server.effects.types;

import java.util.List;

/**
 * Created by limpygnome on 06/05/15.
 */
public abstract class AbstractEffect
{
    public byte type;

    public AbstractEffect(byte type)
    {
        this.type = type;
    }

    public void writePacketData(List<Object> packetData)
    {
        packetData.add(type);
        writeCustomData(packetData);
    }

    public abstract void writeCustomData(List<Object> packetData);
}
