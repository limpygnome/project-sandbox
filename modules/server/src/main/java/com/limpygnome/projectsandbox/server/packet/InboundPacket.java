package com.limpygnome.projectsandbox.server.packet;

import com.limpygnome.projectsandbox.server.Controller;
import java.nio.ByteBuffer;

import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public abstract class InboundPacket extends Packet
{
    /**
     * Parses an inbound packet.
     *
     * @param controller Current instance of Controller
     * @param socket The origin of the message
     * @param playerInfo The player associated with the socket; can be null for packets outside of a session
     * @param bb Used to read primitives from the packet
     * @param data The packet data
     * @throws PacketParseException Thrown if the packet cannot be parsed
     */
    public abstract void parse(Controller controller, WebSocket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException;

    public PlayerInfo fetchPlayer(Controller controller, WebSocket socket)
    {
        PlayerInfo playerInfo = controller.playerManager.getPlayerByWebSocket(socket);

        if (playerInfo == null)
        {
            // TODO: log error; this is critical tampering...or debug? can this situation occur without session?
            socket.close();
            throw new RuntimeException("Attempted to fetch player when no player is associated with socket");
        }

        return playerInfo;
    }
}
