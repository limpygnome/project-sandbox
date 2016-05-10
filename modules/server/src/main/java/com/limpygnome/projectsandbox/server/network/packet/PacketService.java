package com.limpygnome.projectsandbox.server.network.packet;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.network.packet.imp.inventory.InventoryItemSelectedInboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.chat.PlayerChatInboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.individual.PlayerMovementInboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.session.SessionErrorCodeOutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.session.SessionIdentifierInboundPacket;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.PlayerService;
import com.limpygnome.projectsandbox.server.player.SessionService;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Responsible for parsing inbound packets.
 */
@Service
public class PacketService
{
    private final static Logger LOG = LogManager.getLogger(PacketService.class);

    @Autowired
    private Controller controller;
    @Autowired
    private PacketStatsManager packetStatsManager;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private PlayerService playerService;

    public void handleInbound(WebSocket socket, ByteBuffer message)
    {
        byte[] data = message.array();

        if (data != null && data.length > 0)
        {
            byte mainType = data[0];
            byte subType = data[1];

            // Increment bytes in
            packetStatsManager.incrementIn(data.length);

            // Fetch the player's info
            PlayerInfo playerInfo = playerService.getPlayerByWebSocket(socket);

            // Check if we're expecting a session packet - always first packet to system!
            if (playerInfo == null)
            {
                // Handle packet outside of session - newly joined user
                handleInboundPacketNonSession(socket, mainType, subType, message, data);
            } else
            {
                // Handle packet within a session
                handleInboundPacketSession(socket, mainType, subType, message, data, playerInfo);
            }
        }
        else
        {
            LOG.debug("received empty packet");
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
                // Probably someone probing this port
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
                GameSession gameSession = sessionService.load(sessPacket.sessionId);

                // Check we found session
                if (gameSession == null)
                {
                    LOG.warn("Game session not found - token: {}", sessPacket.sessionId);

                    // Send packet regarding session not found
                    SessionErrorCodeOutboundPacket sessionErrorCodeOutboundPacket = new SessionErrorCodeOutboundPacket(
                            SessionErrorCodeOutboundPacket.ErrorCodeType.SESSION_NOT_FOUND
                    );
                    socket.send(sessionErrorCodeOutboundPacket.getPacketData().build());

                    // Kill the socket
                    socket.close();
                    return;
                }

                // Set to connected
                gameSession.setConnected(true);
                sessionService.persist(gameSession);

                // Log event
                LOG.info("Session mapped - {} <> {}", gameSession.getToken(), socket.getRemoteSocketAddress());

                // Register player
                if (playerService.register(socket, gameSession) == null)
                {
                    LOG.error("Failed to register player - sid: {}", gameSession.getToken());
                    socket.close();
                }
                return;
            }
        }

        // Some other packet / invalid data / no session
        LOG.debug("unknown data, closing socket - main type: {}, sub type: {}", mainType, subType);
        socket.close();
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

    public void send(PlayerInfo player, OutboundPacket packet)
    {
        // Check socket not closed
        if (!player.socket.isClosed())
        {
            try
            {
                // Build/fetch packet data
                byte[] packetData = packet.build();

                // Send data
                player.socket.send(packetData);

                // Increment outbound data
                packetStatsManager.incrementOut(packetData.length);
            }
            catch (WebsocketNotConnectedException e)
            {
                LOG.debug("Failed to send data to player", e);
            }
        }
    }

}
