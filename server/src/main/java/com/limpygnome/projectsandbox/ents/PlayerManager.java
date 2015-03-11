    
package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.packets.outbound.EntityUpdatesPacket;
import com.limpygnome.projectsandbox.packets.outbound.PlayerIdentityPacket;
import java.io.IOException;
import java.util.HashMap;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public class PlayerManager
{
    private final Controller controller;
    private final HashMap<WebSocket,Short> playerEntityMappings;
    
    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.playerEntityMappings = new HashMap<>();
    }
    
    public void register(WebSocket ws)
    {
        // Create new player
        Player ply = new Player(controller);
        
        // Create mapping for entity
        if(!controller.entityManager.add(ply))
        {
            ws.close();
            System.err.println("Player manager - failed to register new player.");
            return;
        }
        
        // Add mapping for player
        playerEntityMappings.put(ws, ply.id);
        
        byte[] data;
        
        // Send texture data
        ws.send(controller.textureManager.texturePacket.getPacketData());
        
        // Send map data
        ws.send(controller.mapManager.main.packet.getPacketData());
        
        // Send update of entire world
        try
        {
            EntityUpdatesPacket packetUpdates = new EntityUpdatesPacket();
            packetUpdates.build(controller.entityManager, true);
            data = packetUpdates.getPacketData();
            ws.send(data);
        }
        catch(IOException e)
        {
            e.printStackTrace(System.err);
        }
        
        // Build identity packet to send to player
        PlayerIdentityPacket packet = new PlayerIdentityPacket();
        packet.writeIdentity(ply);
        data = packet.getPacketData();
        
        // Send identity packet
        ws.send(data);
        
        System.out.println("Player manager - mapped " + ws.getRemoteSocketAddress() + " <> " + ply.id);
    }
    public synchronized void unregister(WebSocket ws)
    {
        // Fetch and remove entity associated with connection
        short id = playerEntityMappings.remove(ws);
        
        // Remove entity
        controller.entityManager.remove(id);
        
        System.out.println("Player manager - unmapped " + ws.getRemoteSocketAddress() + " <> " + id);
    }
}
