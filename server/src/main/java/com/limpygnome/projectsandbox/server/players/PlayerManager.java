    
package com.limpygnome.projectsandbox.server.players;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.ents.respawn.pending.PendingRespawn;
import com.limpygnome.projectsandbox.server.ents.types.living.Player;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.ents.EntityUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.players.global.PlayerEventsUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.packets.types.players.individual.PlayerIdentityOutboundPacket;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.limpygnome.projectsandbox.server.utils.IdCounterProvider;
import com.limpygnome.projectsandbox.server.utils.counters.IdCounterConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

/**
 * Responsible for holding data representing the actual players, rather than their entities.
 */
public class PlayerManager implements IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(PlayerManager.class);

    private final Controller controller;
    private final HashMap<WebSocket, PlayerInfo> mappings;
    private final HashMap<Short, PlayerInfo> mappingsById;
    private final HashSet<UUID> connectedRegisteredPlayers;
    private final IdCounterProvider idCounterProvider;

    public PlayerManager(Controller controller)
    {
        this.controller = controller;
        this.mappings = new HashMap<>();
        this.mappingsById = new HashMap<>();
        this.connectedRegisteredPlayers = new HashSet<>();
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
            // Check a registered user is not already connected
            if (session.registeredPlayerId != null && connectedRegisteredPlayers.contains(session.registeredPlayerId))
            {
                // TODO: this needs to throw an exception, detailing why the user cannot connect
                return null;
            }

            // Generate new identifier
            Short playerId = idCounterProvider.nextId(null);

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

            // Inform server player has joined
            PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
            playerEventsUpdatesOutboundPacket.writePlayerJoined(playerInfo);
            broadcast(playerEventsUpdatesOutboundPacket);

            // Give the user all of the users and metrics/stats thus far
            PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundSnapshotPacket = new PlayerEventsUpdatesOutboundPacket();
            writePlayersJoined(playerEventsUpdatesOutboundSnapshotPacket);
            writePlayerMetrics(playerEventsUpdatesOutboundSnapshotPacket, true);
            playerEventsUpdatesOutboundSnapshotPacket.send(playerInfo);

            // Create entity for player
            Entity entityPlayer = playerEntCreate(playerInfo);

            // Spawn the player
            controller.respawnManager.respawn(new EntityPendingRespawn(entityPlayer));

            // Send map data
            controller.mapManager.main.packet.send(playerInfo);

            // Send update of entire world to the player
            EntityUpdatesOutboundPacket packetUpdates = new EntityUpdatesOutboundPacket();
            packetUpdates.build(controller.entityManager, true);
            packetUpdates.send(playerInfo);

            // Send previous chat messages
            controller.chatManager.sendPreviousMessages(playerInfo);

            LOG.info(
                    "Player joined - sid: {}, reg id: {}, ply id: {}",
                    session.sessionId,
                    session.registeredPlayerId,
                    playerId
            );

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
                LOG.debug("Removed entity for disconnecting player - ent id: {}", ent.id);
            }

            // Remove socket mapping
            mappings.remove(ws);

            // Remove ID mapping
            mappingsById.remove(playerInfo.playerId);

            // Remove from connected registered players (if registered)
            if (connectedRegisteredPlayers.contains(playerInfo.session.registeredPlayerId != null))
            {
                connectedRegisteredPlayers.remove(playerInfo.session.registeredPlayerId);
            }

            // Persist player data
            playerInfo.session.playerData.persist();

            // Inform server the player has left
            PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
            playerEventsUpdatesOutboundPacket.writePlayerLeft(playerInfo);
            broadcast(playerEventsUpdatesOutboundPacket);

            LOG.info(
                    "Player left - sid: {}, reg id: {}, ply id: {}",
                    playerInfo.session.sessionId,
                    playerInfo.session.registeredPlayerId,
                    playerInfo.playerId
            );
        }
        else
        {
            LOG.warn("Attempted to unregister unknown player - socket addr: {}", ws.getRemoteSocketAddress());
        }
    }

    public synchronized void logic()
    {
        // Build updates of players
        PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
        writePlayerMetrics(playerEventsUpdatesOutboundPacket, false);

        // Send updates to everyone
        broadcast(playerEventsUpdatesOutboundPacket);
    }

    private synchronized void writePlayersJoined(PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket)
    {
        for (PlayerInfo playerInfo : mappings.values())
        {
            playerEventsUpdatesOutboundPacket.writePlayerJoined(playerInfo);
        }
    }

    private synchronized void writePlayerMetrics(PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket, boolean forced)
    {
        for (PlayerInfo playerInfo : mappings.values())
        {
            playerEventsUpdatesOutboundPacket.writePlayerInfoUpdates(playerInfo, forced);
        }
    }

    @Override
    public synchronized boolean containsId(short id)
    {
        return mappingsById.containsKey(id);
    }

    /**
     * Creates a new instance of a player.
     *
     * @param playerInfo The player's info.
     * @return An instance of an entity.
     */
    public synchronized Entity playerEntCreate(PlayerInfo playerInfo)
    {
        // Create Entity
        return new Player(controller, playerInfo);
    }

    public synchronized void setPlayerEnt(PlayerInfo playerInfo, Entity entity)
    {
        Entity currentEntity = playerInfo.entity;

        if (currentEntity != null)
        {
            // Remove entity if instance of player
            // TODO: needs decoupling...should not have a special case for Player types
            if (currentEntity instanceof Player)
            {
                controller.entityManager.remove(currentEntity);
            }
        }
        
        // Update entity
        playerInfo.entity = entity;
        
        // Create packet to update ID for clientside
        try
        {
            PlayerIdentityOutboundPacket packet = new PlayerIdentityOutboundPacket();
            packet.writeIdentity(playerInfo);
            packet.send(playerInfo);
        }
        catch (IOException e)
        {
            LOG.error("Failed to set entity for player", e);
        }
    }

    public synchronized PlayerInfo getPlayerByWebSocket(WebSocket ws)
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
