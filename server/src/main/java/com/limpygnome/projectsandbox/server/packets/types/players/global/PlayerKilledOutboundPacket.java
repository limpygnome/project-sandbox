package com.limpygnome.projectsandbox.server.packets.types.players.global;

import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author limpygnome
 */
public class PlayerKilledOutboundPacket extends OutboundPacket
{
    public PlayerKilledOutboundPacket()
    {
        super((byte)'P', (byte)'K');
    }
    
    public void writePlayerKilled(AbstractKiller killer, Entity ply) throws IOException
    {
        packetData.add(ply.id);
        packetData.add(killer.causeText());
    }
}
