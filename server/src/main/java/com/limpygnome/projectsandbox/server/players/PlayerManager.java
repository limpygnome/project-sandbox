    
package com.limpygnome.projectsandbox.server.players;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.types.Player;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.ents.EntityUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.players.PlayerIdentityOutboundPacket;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public class PlayerManager
{
    private final static Logger LOG = LogManager.getLogger(PlayerManager.class);

    private final Controller controller;
    private final HashMap<WebSocket, PlayerInfo> mappings;

    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.mappings = new HashMap<>();
    }
    
    public PlayerInfo register(WebSocket ws, Session session)
    {
        try
        {
            // Create new player
            PlayerInfo playerInfo = new PlayerInfo(ws, session);

            // Add mapping for sock
            mappings.put(ws, playerInfo);

            // Create and spawn entity for player
            createAndSpawnNewPlayerEnt(playerInfo);

            // Send map data
            controller.mapManager.main.packet.send(playerInfo);

            // Send update of entire world
            EntityUpdatesOutboundPacket packetUpdates = new EntityUpdatesOutboundPacket();
            packetUpdates.build(controller.entityManager, true);
            packetUpdates.send(playerInfo);

            LOG.info("Mapped - sid: {}", session.sessionId);

            return playerInfo;
        }
        catch(IOException e)
        {
            LOG.error("Failed to register player", e);
            return null;
        }
    }
    public synchronized void unregister(WebSocket ws)
    {
        // Fetch and remove entity associated with connection if player
        PlayerInfo playerInfo = mappings.get(ws);
        Entity ent = playerInfo.entity;
        
        if (playerInfo != null && ent != null && ent instanceof Player)
        {
            controller.entityManager.remove(ent);
            LOG.info("Unmapped - sid: {}", playerInfo.session.sessionId);
        }
    }
    
    public synchronized Player createAndSpawnNewPlayerEnt(PlayerInfo playerInfo)
    {
        Player ply = createNewPlayerEnt(playerInfo);
        controller.mapManager.main.spawn(ply);
        return ply;
    }
    
    public synchronized Player createNewPlayerEnt(PlayerInfo playerInfo)
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
        try
        {
            PlayerIdentityOutboundPacket packet = new PlayerIdentityOutboundPacket();
            packet.writeIdentity(entity);
            packet.send(playerInfo);
        }
        catch (IOException e)
        {
            LOG.error("Failed to set entity for player", e);
        }
    }

    public PlayerInfo getPlayerByWebSocket(WebSocket ws)
    {
        return mappings.get(ws);
    }

    public synchronized void broadcast(OutboundPacket outboundPacket)
    {
        for (Map.Entry<WebSocket, PlayerInfo> kv : mappings.entrySet())
        {
            outboundPacket.send(kv.getValue());
        }
    }
    
}
