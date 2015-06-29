package com.limpygnome.projectsandbox.server.effects.types;

import com.limpygnome.projectsandbox.server.packets.PacketData;

/**
 * Created by limpygnome on 26/06/15.
 */
public class ExplosionEffect extends AbstractEffect
{
    public enum SubType
    {
        ALLAH_ACKBAR((byte) 100)
        ;

        private final byte SUB_TYPE;

        SubType(byte SUB_TYPE)
        {
            this.SUB_TYPE = SUB_TYPE;
        }
    }

    public float x;
    public float y;
    public short subType;

    /**
     *
     * @param x
     * @param y
     * @param subType The sub-type of explosion. Used instead of sending particles/speed etc.
     */
    public ExplosionEffect(float x, float y, SubType subType)
    {
        super((byte) 'E');

        this.x = x;
        this.y = y;
        this.subType = subType.SUB_TYPE;
    }

    @Override
    public void writeCustomData(PacketData packetData)
    {
        packetData.add(x);
        packetData.add(y);
        packetData.add(subType);
    }
}
