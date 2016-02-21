package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packet.imp.player.chat.PlayerChatOutboundPacket;

import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles chat messages.
 */
@Service
public class ChatService
{
    private static final int CHAT_BUFFER_SIZE = 20;

    @Autowired
    private Controller controller;

    private LinkedList<PlayerChatOutboundPacket> chatMessageBuffer;

    public ChatService()
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
            controller.packetService.send(playerInfo, packet);
        }
    }

}
