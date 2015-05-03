package com.limpygnome.projectsandbox.server.packets.types.players;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

/**
 * A packet sent by the client to update their movement slotState.
 * 
 * @author limpygnome
 */
public class PlayerMovementInboundPacket extends InboundPacket
{
    private final static Logger LOG = LogManager.getLogger(PlayerMovementInboundPacket.class);

    public short id;
    public short keys;

    @Override
    public void parse(Controller controller, WebSocket socket, ByteBuffer bb, byte[] data)
    {
        // Parse data
        id = bb.getShort(2);
        keys = bb.getShort(4);
        
        // Fetch player
        PlayerInfo playerInfo = fetchPlayer(controller, socket);
        
        if (playerInfo != null)
        {
            // Check the socket is allowed to update the player, or drop them...
            // TODO: check player can move player - MAJOR SECURITY RISK
            
            // Update the movement
            playerInfo.keys = keys;
            
            LOG.debug("Movement - ent id: {}, flags: {}", id, keys);
        }
        else
        {
            // Potential tampering...
            LOG.warn("Invalid entity - ip: {}, ent id: {}", socket.getRemoteSocketAddress(), id);
        }
    }
}
