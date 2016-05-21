package com.limpygnome.projectsandbox.server.network.packet.imp.player.individual;

import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

import java.io.IOException;

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
