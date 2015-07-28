package com.limpygnome.projectsandbox.server.packet.imp.player.chat;

import com.limpygnome.projectsandbox.server.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.packet.datatype.StringDataType;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

/**
 * Constructs an outbound chat message.
 */
public class PlayerChatOutboundPacket extends OutboundPacket
{
    public PlayerChatOutboundPacket()
    {
        super((byte) 'P', (byte) 'C');
    }

    public void writeChatMessage(PlayerChatInboundPacket packet)
    {
        writeChatMessage(packet.playerInfo, packet.message);
    }

    public void writeChatMessage(PlayerInfo playerInfo, String message)
    {
        packetData.add(playerInfo.playerId);
        packetData.add(message, StringDataType.LengthSize.LENGTH_16);
    }
}
