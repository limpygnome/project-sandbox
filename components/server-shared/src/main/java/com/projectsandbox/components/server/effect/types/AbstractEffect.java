package com.projectsandbox.components.server.effect.types;

import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.network.packet.PacketData;

import java.io.Serializable;

/**
 * Created by limpygnome on 06/05/15.
 */
public abstract class AbstractEffect implements Serializable
{
    private static final long serialVersionUID = 1L;

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

    /**
     * Used to calculate if an effect is within rendering distance of the provided position.
     *
     * @param position the position of rendering
     * @param renderDistance the distance within which the effect would be rendered
     * @return true = within range, false = not within range
     */
    public abstract boolean isWithinRenderDistance(Vector2 position, float renderDistance);

}
