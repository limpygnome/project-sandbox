package com.limpygnome.projectsandbox.website.packets.outbound;

import com.limpygnome.projectsandbox.website.ents.Entity;
import com.limpygnome.projectsandbox.website.packets.OutboundPacket;
import com.limpygnome.projectsandbox.website.packets.OutboundPacket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author limpygnome
 */
public class PlayerIdentityPacket extends OutboundPacket
{
    public PlayerIdentityPacket()
    {
        super((byte)'P', (byte)'I');
    }
    
    public void writeIdentity(Entity ply)
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(ply.id);
        try
        {
            buffer.write(bb.array());
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
}
