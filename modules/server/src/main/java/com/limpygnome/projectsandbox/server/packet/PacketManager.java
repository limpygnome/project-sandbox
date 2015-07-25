package com.limpygnome.projectsandbox.server.packet;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packet.imp.inventory.InventoryItemSelectedInboundPacket;
import com.limpygnome.projectsandbox.server.packet.imp.player.chat.PlayerChatInboundPacket;
import com.limpygnome.projectsandbox.server.packet.imp.player.individual.PlayerMovementInboundPacket;
import com.limpygnome.projectsandbox.server.packet.imp.session.SessionIdentifierInboundPacket;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Responsible for parsing inbound packets.
 */
public class PacketManager
{
    private final static Logger LOG = LogManager.getLogger(PacketManager.class);

    private Controller controller;

    public PacketManager(Controller controller)
    {
        this.controller = controller;
    }

    public void handleInbound(WebSocket socket, ByteBuffer message)
    {
        byte[] data = message.array();
        byte mainType = data[0];
        byte subType = data[1];

        // Fetch the player's info
        PlayerInfo playerInfo = controller.playerManager.getPlayerByWebSocket(socket);

        // Check if we're expecting a session packet - always first packet to system!
        if (playerInfo == null)
        {
            // Handle packet outside of session - newly joined user
            handleInboundPacketNonSession(socket, mainType, subType, message, data);
        }
        else
        {
            // Handle packet within a session
            handleInboundPacketSession(socket, mainType, subType, message, data, playerInfo);
        }
    }

    private void handleInboundPacketNonSession(WebSocket socket, byte mainType, byte subType, ByteBuffer message, byte[] data)
    {
        // Check we have received session packet
        if (mainType == 'P' && subType == 'S')
        {
            // Parse packet and load session associated with player
            SessionIdentifierInboundPacket sessPacket = new SessionIdentifierInboundPacket();
            sessPacket.parse(controller, socket, null, message, data);

            // Check data / socket valid
            if (socket.isClosed())
            {
                LOG.debug("Socket closed prematurely");
                return;
            }
            else if (sessPacket.sessionId == null)
            {
                LOG.warn("Client session packet missing or contains malformed session ID");
            }
            else
            {
                // Load session data from database
                Session session = Session.load(sessPacket.sessionId);

                // Check we found session
                if (session == null)
                {
                    LOG.warn("Session not found - sid: {}", session.sessionId);
                    socket.close();
                    return;
                }

                // Log event
                LOG.info("Session mapped - {} <> {}", session.sessionId, socket.getRemoteSocketAddress());

                // Register player
                if (controller.playerManager.register(socket, session) == null)
                {
                    LOG.error("Failed to register player - sid: {}", session.sessionId);
                    socket.close();
                }
                return;
            }
        }

        // Some other packet / invalid data / no session
        // TODO: add debug msg; nothing else. could be attack...

        socket.close();
        return;
    }

    private void handleInboundPacketSession(WebSocket socket, byte mainType, byte subType, ByteBuffer message, byte[] data, PlayerInfo playerInfo)
    {
        // Create packet based on imp
        InboundPacket packet = null;

        switch(mainType)
        {
            // Players
            case 'P':
                switch (subType)
                {
                    case 'M':
                        // Movement/update packet
                        packet = new PlayerMovementInboundPacket();
                        break;
                    case 'C':
                        // Chat message
                        packet = new PlayerChatInboundPacket();
                        break;
                }
                break;
            // Inventory
            case 'I':
                switch (subType)
                {
                    case 'S':
                        // Inventory item selected packet
                        packet = new InventoryItemSelectedInboundPacket();
                        break;
                }
                break;
        }

        // Check we found a packet
        if(packet == null)
        {
            LOG.error("Unhandled message - type: {}, sub-type: {}", mainType, subType);
            return;
        }

        // Parse data
        try
        {
            packet.parse(controller, socket, playerInfo, message, data);
        }
        catch (PacketParseException e)
        {
            LOG.warn("Failed to parse inbound packet", e);
        }
    }

}
