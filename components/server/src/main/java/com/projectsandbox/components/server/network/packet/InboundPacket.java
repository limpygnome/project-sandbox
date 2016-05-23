package com.projectsandbox.components.server.network.packet;

import com.projectsandbox.components.server.Controller;
import java.nio.ByteBuffer;

import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.player.PlayerInfo;

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
    public abstract void parse(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketParseException;

    public PlayerInfo fetchPlayer(Controller controller, Socket socket)
    {
        PlayerInfo playerInfo = controller.playerService.getPlayerBySocket(socket);

        if (playerInfo == null)
        {
            // TODO: log error; this is critical tampering...or debug? can this situation occur without session?
            socket.close();
            throw new RuntimeException("Attempted to fetch player when no player is associated with socket");
        }

        return playerInfo;
    }
}
