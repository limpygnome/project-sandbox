package com.limpygnome.projectsandbox.server.effects.types;

import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.packets.PacketData;

import java.util.List;

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
