package com.projectsandbox.components.server.effect.types;

import com.projectsandbox.components.server.network.packet.PacketData;

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

    public void writePacketData(PacketData packetData)
    {
        packetData.add(type);
        writeCustomData(packetData);
    }

    public abstract void writeCustomData(PacketData packetData);
}
