package com.limpygnome.projectsandbox.server.packets.types.players.chat;

import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.packets.datatypes.StringDataType;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

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
