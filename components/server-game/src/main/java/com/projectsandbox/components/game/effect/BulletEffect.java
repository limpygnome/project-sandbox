package com.projectsandbox.components.game.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.network.packet.PacketData;

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
    public void writeCustomData(PacketData packetData)
    {
        packetData.add(x);
        packetData.add(y);
    }

    @Override
    public boolean isWithinRenderDistance(Vector2 position, float renderDistance)
    {
        float distance = Vector2.distance(position, new Vector2(x, y));
        return distance <= renderDistance;
    }

}
