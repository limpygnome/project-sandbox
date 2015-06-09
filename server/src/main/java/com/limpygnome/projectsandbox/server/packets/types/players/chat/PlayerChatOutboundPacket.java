package com.limpygnome.projectsandbox.server.packets.types.players.chat;

import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

/**
 * Created by limpygnome on 09/06/15.
 */
public class PlayerChatOutboundPacket extends OutboundPacket
{
    public PlayerChatOutboundPacket()
    {
        super((byte) 'P', (byte) 'C');
    }

    public void writeChatMessage(PlayerInfo playerInfo, String message)
    {
        packetData.add(playerInfo.playerId);
        packetData.add(message);
    }
}
