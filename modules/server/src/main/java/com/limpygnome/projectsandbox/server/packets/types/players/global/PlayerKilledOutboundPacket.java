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
        PlayerInfo[] playerInfoKillers = killer.killer != null ? killer.killer.getPlayers() : null;
        PlayerInfo playerInfoKiller = playerInfoKillers != null && playerInfoKillers.length > 0 ? playerInfoKillers[0] : null;

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
        if (killer.killer != null)
        {
            packetData.add(killer.killer.id);
        }
        else
        {
            // TODO: this should be optional in packet, using flags
            packetData.add(0);
        }

        packetData.add(victim.playerId);

        if (playerInfoKiller != null)
        {
            packetData.add(playerInfoKiller.playerId);
        }

    }
}