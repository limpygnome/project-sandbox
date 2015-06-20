package com.limpygnome.projectsandbox.server.players;

import com.limpygnome.projectsandbox.server.packets.types.players.chat.PlayerChatOutboundPacket;

import java.util.LinkedList;

/**
 * Created by limpygnome on 20/06/15.
 */
public class ChatManager
{
    private static final int CHAT_BUFFER_SIZE = 10;

    private LinkedList<PlayerChatOutboundPacket> chatMessageBuffer;

    public ChatManager()
    {
        this.chatMessageBuffer = new LinkedList<>();
    }

    public synchronized void add(PlayerChatOutboundPacket packet)
    {
        // Remove first item, so that the list is limited to the size
        if (chatMessageBuffer.size() > CHAT_BUFFER_SIZE)
        {
            chatMessageBuffer.removeFirst();
        }

        chatMessageBuffer.add(packet);
    }

    public synchronized void sendPreviousMessages(PlayerInfo playerInfo)
    {
        for (PlayerChatOutboundPacket packet : chatMessageBuffer)
        {
            packet.send(playerInfo);
        }
    }
}
