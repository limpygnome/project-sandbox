package com.limpygnome.projectsandbox.server.packet.imp.player.chat;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packet.InboundPacket;
import com.limpygnome.projectsandbox.server.packet.PacketParseException;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import org.java_websocket.WebSocket;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Used to parse an inbound chat message from a player.
 */
public class PlayerChatInboundPacket extends InboundPacket
{

    public PlayerInfo playerInfo;
    public String message;

    @Override
    public void parse(Controller controller, WebSocket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException
    {
        // All messgaes should be at least 6 bytes:
        // - mapMain type (1 byte)
        // - subtype (1 byte)
        // - length of message (2 bytes)
        // - at least one character (1 bytes)
        if (data.length < 5)
        {
            throw new PacketParseException("Incorrect length", data);
        }

        this.playerInfo = playerInfo;

        // Read length of message and validate
        short messageLength = bb.getShort(2);

        if (data.length != 4 + messageLength)
        {
            throw new PacketParseException("Length of chat message is invalid", data);
        }

        // Parse message
        try
        {
            this.message = new String(data, 4, messageLength, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Unable to use UTF-16 encoder");
        }

        // Validate chars
        // TODO: consider if we should do white-listing for chars instead
        for (char msgChar : this.message.toCharArray())
        {
            if (msgChar < 32)
            {
                throw new RuntimeException("Non-printable char in message, rejecting");
            }
        }

        // Build and broadcast chat message
        PlayerChatOutboundPacket playerChatOutboundPacket = new PlayerChatOutboundPacket();
        playerChatOutboundPacket.writeChatMessage(this);

        controller.playerManager.broadcast(playerChatOutboundPacket);

        // Add to chat buffer
        controller.chatManager.add(playerChatOutboundPacket);
    }

}
