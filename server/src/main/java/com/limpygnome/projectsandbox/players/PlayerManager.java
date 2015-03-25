    
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
    
    private final HashMap<WebSocket, PlayerInfo> mappingsSocket;
    private final HashMap<Short, PlayerInfo> mappingsEnt;
    
    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.mappingsSocket = new HashMap<>();
        this.mappingsEnt = new HashMap<>();
    }
    
    public void register(WebSocket ws)
    {
        // Create new player
        PlayerInfo playerInfo = new PlayerInfo(ws);
        
        // Add mapping for sock
        mappingsSocket.put(ws, playerInfo);
        
        // Create entity for player
        // TODO: set to use spawns with type of player created etc?
        createSetNewPlayerEnt(playerInfo, new Vector2(0.0f, 0.0f));
        
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
    }
    public synchronized void unregister(WebSocket ws)
    {
        // Fetch and remove entity associated with connection if player
        PlayerInfo playerInfo = mappingsSocket.get(ws);
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
    
    public synchronized void createSetNewPlayerEnt(PlayerInfo playerInfo, Vector2 position)
    {
        // Create new entity
        Player ply = new Player(controller, playerInfo);
        ply.position(position);
        
        // Add entity to world
        if(!controller.entityManager.add(ply))
        {
            System.err.println("Player manager - failed to register new player.");
            return;
        }
        
        // Set player to use ent
        setPlayerEnt(playerInfo, ply);
    }
    
    public synchronized void setPlayerEnt(PlayerInfo playerInfo, Entity entity)
    {
        Entity current = playerInfo.entity;
        if (current != null)
        {
            // Remove old mapping
            mappingsEnt.remove(current.id);
            
            // Remove entity if instance of player
            if (current instanceof Player)
            {
                controller.entityManager.remove(current);
            }
        }
        
        // Add new mapping
        mappingsEnt.put(entity.id, playerInfo);
        
        // Update entity
        playerInfo.entity = entity;
        
        // Create packet to update ID for clientside
        PlayerIdentityPacket packet = new PlayerIdentityPacket();
        packet.writeIdentity(entity);
        byte[] data = packet.getPacketData();
        
        // Send identity packet
        playerInfo.socket.send(data);
    }
    
    public PlayerInfo getPlayerByEntId(short id)
    {
        return mappingsEnt.get(id);
    }
    
}
