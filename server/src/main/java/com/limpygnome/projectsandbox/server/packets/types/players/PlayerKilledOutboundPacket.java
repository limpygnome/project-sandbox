package com.limpygnome.projectsandbox.server.packets.types.players;

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
        byte[] causeTextRaw = killer.causeText().getBytes("UTF-8");
        int causeTextLength = causeTextRaw.length;

        ByteBuffer bb = ByteBuffer.allocate(2 + 1 + causeTextLength);
        bb.putShort(ply.id);
        bb.put((byte) causeTextLength);
        bb.put(causeTextRaw);
        buffer.write(bb.array());
    }
}
