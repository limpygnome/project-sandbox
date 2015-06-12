package com.limpygnome.projectsandbox.server.packets.types.players.global;

import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author limpygnome
 */
public class PlayerKilledOutboundPacket extends OutboundPacket
{
    private enum PlayerKilledFlags
    {
        FIELD_PLAYERID_KILLER((byte) 1);

        private final byte mask;

        PlayerKilledFlags(byte mask)
        {
            this.mask = mask;
        }
    }

    public PlayerKilledOutboundPacket()
    {
        super((byte)'P', (byte)'K');
    }
    
    public void writePlayerKilled(AbstractKiller killer, PlayerInfo victim) throws IOException
    {
        PlayerInfo playerInfoKiller = killer.getPlayerKiller();

        // Build flags
        byte flag = 0;

        if (playerInfoKiller != null)
        {
            flag |= PlayerKilledFlags.FIELD_PLAYERID_KILLER.mask;
        }

        // Add items
        packetData.add(flag);
        packetData.add(killer.causeText());

        packetData.add(killer.victim.id);
        packetData.add(killer.killer.id);

        packetData.add(victim.playerId);

        if (playerInfoKiller != null)
        {
            packetData.add(playerInfoKiller.playerId);
        }

    }
}
