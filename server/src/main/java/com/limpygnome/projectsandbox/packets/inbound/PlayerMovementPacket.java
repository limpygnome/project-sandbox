package com.limpygnome.projectsandbox.packets.inbound;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.packets.InboundPacket;
import com.limpygnome.projectsandbox.players.PlayerInfo;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;

/**
 * A packet sent by the client to update their movement state.
 * 
 * @author limpygnome
 */
public class PlayerMovementPacket extends InboundPacket
{
    public short id;
    public byte keys;
    
    public PlayerMovementPacket()
    {
        super((byte)'P', (byte)'U');
    }

    @Override
    public void parse(Controller controller, WebSocket ws, ByteBuffer bb, byte[] data)
    {
        // Parse data
        id = bb.getShort(2);
        keys = data[4];
        
        // Fetch player
        PlayerInfo playerInfo = controller.playerManager.getPlayerByEntId(id);
        
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
