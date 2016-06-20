package com.projectsandbox.components.game.effect;

import com.projectsandbox.components.server.effect.types.AbstractEffect;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.network.packet.PacketData;

/**
 * Created by limpygnome on 26/06/15.
 */
public class ExplosionEffect extends AbstractEffect
{
    public enum SubType
    {
        SUICIDE_VEST((byte) 100),
        JUMP_DRIVE((byte) 101),
        FORCE_FIELD((byte)102)
        ;

        private final byte SUB_TYPE;

        SubType(byte SUB_TYPE)
        {
            this.SUB_TYPE = SUB_TYPE;
        }
    }

    public byte subType;
    public float x;
    public float y;

    /**
     *
     * @param x
     * @param y
     * @param subType The sub-type of explosion. Used instead of sending particles/speed etc.
     */
    public ExplosionEffect(float x, float y, SubType subType)
    {
        super((byte) 'E');

        this.subType = subType.SUB_TYPE;
        this.x = x;
        this.y = y;
    }

    @Override
    public void writeCustomData(PacketData packetData)
    {
        packetData.add(subType);
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
