package com.limpygnome.projectsandbox.server.packets.types.players;

import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.ents.Entity;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A packet used to set the current Entity which belongs to the player.
 *
 * @author limpygnome
 */
public class PlayerIdentityOutboundPacket extends OutboundPacket
{
    public PlayerIdentityOutboundPacket()
    {
        super((byte)'P', (byte)'I');
    }
    
    public void writeIdentity(Entity ply) throws IOException
    {
        packetData.add(ply.id);
    }
}
