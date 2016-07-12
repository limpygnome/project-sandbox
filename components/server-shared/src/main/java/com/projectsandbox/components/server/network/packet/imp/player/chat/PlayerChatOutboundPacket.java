package com.projectsandbox.components.server.network.packet.imp.player.chat;

import com.projectsandbox.components.server.network.packet.OutboundPacket;
import com.projectsandbox.components.server.player.PlayerInfo;

/**
 * Constructs an outbound chat message.
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
        packetData.addUtf8(playerInfo.session.getNickname());
        packetData.addUtf8(message);
    }

}
