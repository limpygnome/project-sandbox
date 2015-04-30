package com.limpygnome.projectsandbox.server.packets.types.players;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;

/**
 * A packet sent by the client to update their movement slotState.
 * 
 * @author limpygnome
 */
public class PlayerMovementInboundPacket extends InboundPacket
{
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
            // TODO: ...
            
            // Update the movement
            playerInfo.keys = keys;
            
            System.out.println("Player movement - ent id: " + id + ", flags: " + keys);
        }
        else
        {
            System.out.println("Player movement - invalid; ent id: " + id);
        }
    }
    
    
    
}
