package com.projectsandbox.components.server.network.packet.imp.player.chat;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.handler.AuthenticatedInboundPacketHandler;
import com.projectsandbox.components.server.network.packet.handler.InboundPacketHandler;
import com.projectsandbox.components.server.network.packet.PacketHandlerException;
import com.projectsandbox.components.server.network.packet.factory.PacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Used to handle an inbound chat message from a player.
 */
@PacketHandler(mainType = 'P', subType = 'C')
public class PlayerChatInboundPacketHandler extends AuthenticatedInboundPacketHandler
{

    @Override
    public void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketHandlerException
    {
        // All messgaes should be at least 6 bytes:
        // - mapMain type (1 byte)
        // - subtype (1 byte)
        // - length of message (2 bytes)
        // - at least one character (1 bytes)
        if (data.length < 5)
        {
            throw new PacketHandlerException("Incorrect length", data);
        }

        // Read length of message and validate
        short messageLength = bb.getShort(2);

        if (data.length != 4 + messageLength)
        {
            throw new PacketHandlerException("Length of chat message is invalid", data);
        }

        // Parse message
        String message;

        try
        {
            message = new String(data, 4, messageLength, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Unable to use UTF-16 encoder");
        }

        // Validate chars
        // TODO: SECURITY - consider if we should do white-listing for chars instead
        for (char msgChar : message.toCharArray())
        {
            if (msgChar < 32)
            {
                throw new RuntimeException("Non-printable char in message, rejecting");
            }
        }

        // Build and broadcast chat message
        PlayerChatOutboundPacket playerChatOutboundPacket = new PlayerChatOutboundPacket();
        playerChatOutboundPacket.writeChatMessage(playerInfo, message);

        controller.playerService.broadcast(playerChatOutboundPacket);

        // Add to chat buffer
        controller.chatService.add(playerChatOutboundPacket);
    }

}
