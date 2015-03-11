package com.limpygnome.projectsandbox.packets.inbound;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.packets.InboundPacket;
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
    public byte movement;
    
    public PlayerMovementPacket()
    {
        super((byte)'P', (byte)'U');
    }

    @Override
    public void parse(Controller controller, WebSocket ws, ByteBuffer bb, byte[] data)
    {
        // Parse data
        id = bb.getShort(2);
        movement = data[4];
        
        // Fetch player
        Entity ent = controller.entityManager.fetch(id);
        if(ent != null && ent instanceof Player)
        {
            Player ply = (Player) ent;
            
            // Check the socket is allowed to update the player, or drop them...
            // TODO: ...
            
            // Update the movement
            ply.movement = movement;
            
            System.out.println("Player movement - ent id: " + id + ", flags: " + movement);
        }
        else
        {
            System.out.println("Player movement - invalid; ent id: " + id);
        }
    }
    
    
    
}
