    
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

import com.limpygnome.projectsandbox.server.utils.IdCounterProvider;
import com.limpygnome.projectsandbox.server.utils.counters.IdCounterConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public class PlayerManager implements IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(PlayerManager.class);

    private final Controller controller;
    private final HashMap<WebSocket, PlayerInfo> mappings;
    private final HashMap<Short, PlayerInfo> mappingsById;
    private final IdCounterProvider idCounterProvider;

    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.mappings = new HashMap<>();
        this.mappingsById = new HashMap<>();
        this.idCounterProvider = new IdCounterProvider(this);
    }

    /**
     * Attempts to register a new player.
     *
     * @param ws The socket
     * @param session The session
     * @return An instance, or null if the player cannot be registered.
     */
    public synchronized PlayerInfo register(WebSocket ws, Session session)
    {
        try
        {
            // Generate new identifier
            Short playerId = idCounterProvider.nextId();

            // Check we got an identifier
            if (playerId == null)
            {
                return null;
            }

            // Create new player
            PlayerInfo playerInfo = new PlayerInfo(ws, session, playerId);

            // Add mapping for sock
            mappings.put(ws, playerInfo);

            // Add mapping for identifier
            mappingsById.put(playerId, playerInfo);

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
        // Fetch associated player, and entity
        PlayerInfo playerInfo = mappings.get(ws);
        Entity ent = playerInfo.entity;
        
        if (playerInfo != null)
        {
            // Remove entity
            if (ent != null && ent instanceof Player)
            {
                controller.entityManager.remove(ent);
            }

            // Remove socket mapping
            mappings.remove(ws);

            // Remove ID mapping
            mappingsById.remove(playerInfo.playerId);

            LOG.info("Unmapped - sid: {}", playerInfo.session.sessionId);
        }
    }

    @Override
    public boolean containsId(short id)
    {
        return mappingsById.containsKey(id);
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
