package com.limpygnome.projectsandbox.server.packets;

import com.limpygnome.projectsandbox.server.Controller;
import java.nio.ByteBuffer;

import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public abstract class InboundPacket extends Packet
{
    public abstract void parse(Controller controller, WebSocket socket, ByteBuffer bb, byte[] data);

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
