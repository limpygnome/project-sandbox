package com.projectsandbox.components.server.network.packet.imp.session;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.handler.InboundPacketHandler;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.projectsandbox.components.server.network.packet.factory.PacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerService;
import com.projectsandbox.components.server.player.SessionService;
import com.projectsandbox.components.shared.model.GameSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A packet used to identify the session to which the socket belongs.
 */
@PacketHandler(mainType = 'P', subType = 'S')
public class SessionIdentificationInboundPacketHandler extends InboundPacketHandler
{
    private final static Logger LOG = LogManager.getLogger(SessionIdentificationInboundPacketHandler.class);

    @Autowired
    private SessionService sessionService;
    @Autowired
    private PlayerService playerService;

    @Override
    public void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data)
    {
        UUID sessionId = parseSessionId(data);

        // Check data / socket valid
        if (!socket.isOpen())
        {
            // Probably someone probing this port
            LOG.debug("socket closed prematurely");
            return;
        }
        else if (sessionId == null)
        {
            LOG.warn("client session packet missing or contains malformed session ID");
        }
        else
        {
            // Load session data from database
            GameSession gameSession = sessionService.load(sessionId);

            // Check we found session
            if (gameSession == null)
            {
                LOG.warn("game session not found - token: {}", sessionId);

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
            LOG.info("session mapped - {} <> {}", gameSession.getToken(), socket.getRemoteSocketAddress());

            // Register player
            if (playerService.register(socket, gameSession) == null)
            {
                LOG.error("failed to register player - sid: {}", gameSession.getToken());
                socket.close();
            }
            return;
        }
    }

    private UUID parseSessionId(byte[] data)
    {
        // TODO: upgrade to 16 bytes, rather than a string; at present, we want this to just be simple, but could be optimised
        UUID sessionId = null;

        // We're expecting a GUUID - ignore first two bytes (mapMain/subtype) + 32 bytes
        if (data.length == (36 + 2))
        {
            try
            {
                // Read 36 bytes and convert to UUID
                String rawUuid = new String(data, 2, 36);

                // Parse into type, essentially validating the data
                sessionId = UUID.fromString(rawUuid);
            }
            catch (Exception e)
            {
                LOG.error("Error parsing session UUID", e);
            }
        }

        return sessionId;
    }

    @Override
    public boolean isPlayerAuthenticated(PlayerInfo playerInfo)
    {
        // Only serve sockets without a player...
        return playerInfo == null;
    }

}
