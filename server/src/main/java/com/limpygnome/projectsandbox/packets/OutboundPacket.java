package com.limpygnome.projectsandbox.packets;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author limpygnome
 */
public abstract class OutboundPacket extends Packet
{
    protected ByteArrayOutputStream buffer;
    
    public OutboundPacket(byte mainType, byte subType)
    {
        super(mainType, subType);
        
        buffer = new ByteArrayOutputStream();
        
        // Write header data
        buffer.write(mainType);
        buffer.write(subType);
    }
    
    public byte[] getPacketData()
    {
        return buffer.toByteArray();
    }
}
