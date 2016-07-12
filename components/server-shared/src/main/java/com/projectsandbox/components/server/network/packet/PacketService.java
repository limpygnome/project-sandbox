package com.projectsandbox.components.server.network.packet;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.factory.PacketFactory;
import com.projectsandbox.components.server.network.packet.handler.InboundPacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerService;
import com.projectsandbox.components.server.player.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    @Autowired
    private PacketFactory packetFactory;

    public void handleInbound(Socket socket, ByteBuffer message)
    {
        boolean closeSocket = false;
        byte[] data = message.array();

        if (data != null && data.length > 0)
        {
            byte mainType = data[0];
            byte subType = data[1];

            // Increment bytes in
            packetStatsManager.incrementIn(data.length);

            // Fetch the player's info
            PlayerInfo playerInfo = playerService.getPlayerBySocket(socket);


            // Parse data
            try
            {
                // Fetch associated packet handler
                InboundPacketHandler packetHandler = packetFactory.getInboundPacket(playerInfo, mainType, subType);

                // Check we found a packet
                if (packetHandler == null)
                {
                    LOG.info("unhandled message, closing socket - type: {}, sub-type: {}", mainType, subType);
                    closeSocket = true;
                }
                else if (!packetHandler.isPlayerAuthenticated(playerInfo))
                {
                    LOG.info("player is not authorised to execute command, closing socket - type: {}, sub-type: {}", mainType, subType);
                }
                else
                {
                    packetHandler.handle(controller, socket, playerInfo, message, data);
                }
            }
            catch (PacketHandlerException e)
            {
                LOG.info("failed to parse inbound packet, closing socket", e);
                closeSocket = true;
            }
        }
        else
        {
            LOG.debug("received empty packet, closing socket");
            closeSocket = true;
        }

        // Check if to close the client's socket
        if (closeSocket)
        {
            socket.close();
        }
    }

    public void send(PlayerInfo player, OutboundPacket packet)
    {
        // Check socket not closed
        if (player.socket.isOpen())
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
