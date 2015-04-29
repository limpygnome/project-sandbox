package com.limpygnome.projectsandbox.server.packets.types.players;

import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.ents.Entity;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author limpygnome
 */
public class PlayerIdentityOutboundPacket extends OutboundPacket
{
    public PlayerIdentityOutboundPacket()
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
