package com.limpygnome.projectsandbox.server.packets;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.types.inventory.InventoryItemSelectedInboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.players.PlayerMovementInboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.session.SessionIdentifierInboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.players.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 29/04/15.
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
            // Check we have received session packet
            if (mainType == 'P' && subType == 'S')
            {
                // Parse packet and load session associated with player
                SessionIdentifierInboundPacket sessPacket = new SessionIdentifierInboundPacket();
                sessPacket.parse(controller, socket, message, data);

                // Check data / socket valid
                if (sessPacket.sessionId != null && socket.isOpen())
                {
                    // Load session data from database
                    // TODO: actually load from DB
                    Session session = new Session();

                    // TODO: remove this stub
                    session.sessionId = sessPacket.sessionId;

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

        // Create packet based on types
        InboundPacket packet = null;

        switch(mainType)
        {
            case 'P':
                switch (subType)
                {
                    case 'M':
                        // Player movement/update packet
                        packet = new PlayerMovementInboundPacket();
                        break;
                }
                break;
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
        packet.parse(controller, socket, message, data);
    }
}
