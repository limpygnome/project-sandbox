package com.limpygnome.projectsandbox.website.packets;

import java.nio.ByteBuffer;

/**
 *
 * @author limpygnome
 */
public abstract class Packet
{
    protected byte mainType;
    protected byte subType;
    
    public Packet(byte mainType, byte subType)
    {
        this.mainType = mainType;
        this.subType = subType;
    }
}
