    
package com.limpygnome.projectsandbox.players;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
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
    
    private final HashMap<WebSocket, PlayerInfo> mappingsSock2Ply;
    
    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.mappingsSock2Ply = new HashMap<>();
    }
    
    public PlayerInfo register(WebSocket ws, Session session)
    {
        // Create new player
        PlayerInfo playerInfo = new PlayerInfo(ws, session);
        
        // Add mapping for sock
        mappingsSock2Ply.put(ws, playerInfo);
        
        // Create and spawn entity for player
        createSpawnNewPlayerEnt(playerInfo);
        
        byte[] data;
        
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
        
        System.out.println("Player manager - mapped " + ws.getRemoteSocketAddress() + " <> " + playerInfo.entity);
        
        return playerInfo;
    }
    public synchronized void unregister(WebSocket ws)
    {
        // Fetch and remove entity associated with connection if player
        PlayerInfo playerInfo = mappingsSock2Ply.get(ws);
        Entity ent = playerInfo.entity;
        
        if (ent != null && ent instanceof Player)
        {
            controller.entityManager.remove(ent);
        }
        
        System.out.println("Player manager - unmapped " + ws.getRemoteSocketAddress() + " <> " + ent);
    }
    
    public synchronized void handleDeath(Entity plyEntity)
    {
    }
    
    public synchronized Player createSpawnNewPlayerEnt(PlayerInfo playerInfo)
    {
        Player ply = createSetNewPlayerEnt(playerInfo);
        controller.mapManager.main.spawn(ply);
        return ply;
    }
    
    public synchronized Player createSetNewPlayerEnt(PlayerInfo playerInfo, Vector2 position)
    {
        Player ply = createSetNewPlayerEnt(playerInfo);
        ply.position(position);
        return ply;
    }
    
    public synchronized Player createSetNewPlayerEnt(PlayerInfo playerInfo)
    {
        // Create new entity
        Player ply = new Player(controller, playerInfo);
        
        // Add entity to world
        if(!controller.entityManager.add(ply))
        {
            // TODO; consider removal or better handling...
            throw new RuntimeException("Failed to spawn player");
        }
        
        // Set player to use ent
        setPlayerEnt(playerInfo, ply);
        
        return ply;
    }
    
    public synchronized void setPlayerEnt(PlayerInfo playerInfo, Entity entity)
    {
        Entity current = playerInfo.entity;
        if (current != null)
        {
            // Remove entity if instance of player
            if (current instanceof Player)
            {
                controller.entityManager.remove(current);
            }
        }
        
        // Update entity
        playerInfo.entity = entity;
        
        // Create packet to update ID for clientside
        PlayerIdentityPacket packet = new PlayerIdentityPacket();
        packet.writeIdentity(entity);
        byte[] data = packet.getPacketData();
        
        // Send identity packet
        playerInfo.socket.send(data);
    }

    public PlayerInfo getPlayerByWebSocket(WebSocket ws)
    {
        return mappingsSock2Ply.get(ws);
    }
    
}
