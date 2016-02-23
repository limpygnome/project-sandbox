package com.limpygnome.projectsandbox.server.packet.imp.player.global;

import com.limpygnome.projectsandbox.server.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.shared.model.User;

/**
 * Used to update the data regarding all players on the server, which is sent to each player.
 */
public class PlayerEventsUpdatesOutboundPacket extends OutboundPacket
{
    public PlayerEventsUpdatesOutboundPacket()
    {
        super((byte) 'P', (byte) 'E');
    }

    public void writePlayerJoined(PlayerInfo playerInfo)
    {
        packetData.add('J');
        packetData.add(playerInfo.playerId);
        packetData.addUtf8(playerInfo.session.getNickname());
    }

    public void writePlayerInfoUpdates(PlayerInfo playerInfo, boolean forced)
    {
        if (forced || playerInfo.session.getPlayerMetrics().isDirtyAndResetDirtyFlag())
        {
            packetData.add('U');
            packetData.add(playerInfo.playerId);

            packetData.add((short) playerInfo.session.getPlayerMetrics().getKills());
            packetData.add((short) playerInfo.session.getPlayerMetrics().getDeaths());
            packetData.add(playerInfo.session.getPlayerMetrics().getScore());
        }
    }

    public void writePlayerLeft(PlayerInfo playerInfo)
    {
        packetData.add('L');
        packetData.add(playerInfo.playerId);
    }

}
