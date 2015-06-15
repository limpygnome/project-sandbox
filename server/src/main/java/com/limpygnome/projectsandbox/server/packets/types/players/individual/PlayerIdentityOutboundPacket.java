package com.limpygnome.projectsandbox.server.packets.types.players.individual;

import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

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
    
    public void writeIdentity(PlayerInfo playerInfo) throws IOException
    {
        packetData.add(playerInfo.playerId);
        packetData.add(playerInfo.entity.id);
    }
}
