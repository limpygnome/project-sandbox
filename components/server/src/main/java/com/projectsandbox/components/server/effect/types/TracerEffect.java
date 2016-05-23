package com.projectsandbox.components.server.effect.types;

import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.network.packet.PacketData;

/**
 * Created by limpygnome on 08/05/15.
 */
public class TracerEffect extends AbstractEffect
{
    public Vector2 start;
    public Vector2 end;

    public TracerEffect(Vector2 start, Vector2 end)
    {
        super((byte) 'T');

        this.start = start;
        this.end = end;
    }

    @Override
    public void writeCustomData(PacketData packetData)
    {
        packetData.add(start.x);
        packetData.add(start.y);
        packetData.add(end.x);
        packetData.add(end.y);
    }
}
