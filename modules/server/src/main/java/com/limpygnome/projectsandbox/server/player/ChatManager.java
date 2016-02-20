package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packet.imp.player.chat.PlayerChatOutboundPacket;

import java.util.LinkedList;
import javax.naming.ldap.Control;

/**
 * Handles chat messages.
 */
public class ChatManager
{
    private static final int CHAT_BUFFER_SIZE = 20;

    private Controller controller;
    private LinkedList<PlayerChatOutboundPacket> chatMessageBuffer;

    public ChatManager(Controller controller)
    {
        this.chatMessageBuffer = new LinkedList<>();
        this.controller = controller;
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
            controller.packetManager.send(playerInfo, packet);
        }
    }

}
