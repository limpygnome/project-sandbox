package com.projectsandbox.components.server.network.packet.handler;

import com.projectsandbox.components.server.Controller;
import java.nio.ByteBuffer;

import com.projectsandbox.components.server.network.Socket;
import com.projectsandbox.components.server.network.packet.Packet;
import com.projectsandbox.components.server.network.packet.PacketHandlerException;
import com.projectsandbox.components.server.player.PlayerInfo;

/**
 *
 * @author limpygnome
 */
public abstract class InboundPacketHandler extends Packet
{

    /**
     * Parses an inbound packet.
     *
     * @param controller Current instance of Controller
     * @param socket The origin of the message
     * @param playerInfo The player associated with the socket; can be null for packets outside of a session
     * @param bb Used to read primitives from the packet
     * @param data The packet data
     * @throws PacketHandlerException Thrown if the packet cannot be parsed
     */
    public abstract void handle(Controller controller, Socket socket, PlayerInfo playerInfo, ByteBuffer bb, byte[] data) throws PacketHandlerException;

    /**
     * Indicates if the player is authenticated for this handler.
     *
     * @param playerInfo the player info
     * @return true = authenticated, false = not authenticated
     */
    public abstract boolean isPlayerAuthenticated(PlayerInfo playerInfo);

}
