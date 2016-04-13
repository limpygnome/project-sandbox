    
package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntityService;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.game.entity.living.Player;
import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;
import com.limpygnome.projectsandbox.server.network.packet.imp.entity.EntityUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.global.PlayerEventsUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.individual.PlayerIdentityOutboundPacket;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.limpygnome.projectsandbox.server.util.IdCounterProvider;
import com.limpygnome.projectsandbox.server.util.counters.IdCounterConsumer;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Responsible for holding data representing the actual players, rather than their entities.
 *
 * TODO: decouple Player class, perhaps config defines player ent or game logic gives us instance.
 */
@Service
public class PlayerService implements EventLogicCycleService, IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(PlayerService.class);

    @Autowired
    private Controller controller;
    @Autowired
    private MapService mapService;
    @Autowired
    private PacketService packetService;
    @Autowired
    private PlayerEntityService playerEntityService;

    private final HashMap<WebSocket, PlayerInfo> mappings;
    private final HashMap<Short, PlayerInfo> mappingsById;
    private final HashSet<UUID> connectedRegisteredPlayers;
    private final IdCounterProvider idCounterProvider;

    public PlayerService()
    {
        this.mappings = new HashMap<>();
        this.mappingsById = new HashMap<>();
        this.connectedRegisteredPlayers = new HashSet<>();
        this.idCounterProvider = new IdCounterProvider(this);
    }

    /**
     * Attempts to register a new player.
     *
     * @param ws The socket
     * @param session The game session
     * @return An instance, or null if the player cannot be registered.
     */
    public PlayerInfo register(WebSocket ws, GameSession session)
    {
        try
        {
            // Check a registered user is not already connected
            // TODO: probably remove this...
            synchronized (this)
            {
                User user = session.getUser();

                if (user != null && connectedRegisteredPlayers.contains(user.getUserId()))
                {
                    // TODO: this needs to throw an exception, detailing why the user cannot connect
                    LOG.warn("Player attempted to connect whilst already in session - user id: {}", user.getUserId());
                    return null;
                }
            }

            // Generate new identifier
            Short playerId = idCounterProvider.nextId(null);

            // Check we got an identifier
            if (playerId == null)
            {
                LOG.error("Player ID provider exhausted");
                return null;
            }

            // Create new player
            PlayerInfo playerInfo = new PlayerInfo(ws, session, playerId);

            synchronized (this)
            {
                // Add mapping for sock
                mappings.put(ws, playerInfo);

                // Add mapping for identifier
                mappingsById.put(playerId, playerInfo);

                LOG.info("Player joined - ply id: {}, name: {}", playerId, session.getNickname());
            }

            // Create, spawn and send data for player
            playerSpawnAndSendData(playerInfo);

            // Give the user all of the users and metrics/stats thus far
            PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundSnapshotPacket = new PlayerEventsUpdatesOutboundPacket();
            writePlayersJoined(playerEventsUpdatesOutboundSnapshotPacket);
            writePlayerMetrics(playerEventsUpdatesOutboundSnapshotPacket, true);
            controller.packetService.send(playerInfo, playerEventsUpdatesOutboundSnapshotPacket);

            // Send previous chat messages
            controller.chatService.sendPreviousMessages(playerInfo);

            LOG.info(
                    "Player joined - session token: {}, user id: {}, ply id: {}",
                    session.getToken(),
                    session.getUser() != null ? session.getUser().getUserId() : null,
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
    public void unregister(WebSocket ws)
    {
        // Fetch associated player, and entity
        PlayerInfo playerInfo;

        synchronized (this)
        {
            playerInfo = mappings.get(ws);
        }

        if (playerInfo != null)
        {
            Entity ent = playerInfo.entity;

            // Persist player's current entity
            playerEntityService.persistPlayer(playerInfo);

            // Remove entity
            // TODO: make this more generic...perhaps remove if spawned entity
            if (ent != null && ent instanceof Player)
            {
                ent.map.entityManager.remove(ent);
                LOG.debug("Removed entity for disconnecting player - ent id: {}", ent.id);
            }

            synchronized (this)
            {
                // Remove socket mapping
                mappings.remove(ws);

                // Remove ID mapping
                mappingsById.remove(playerInfo.playerId);

                // Remove from connected registered players (if registered)
                User user = playerInfo.session.getUser();

                if (user != null)
                {
                    connectedRegisteredPlayers.remove(user.getUserId());
                }

                // Unload game session
                controller.sessionService.unload(playerInfo.session);

                // Inform server the player has left
                PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
                playerEventsUpdatesOutboundPacket.writePlayerLeft(playerInfo);
                broadcast(playerEventsUpdatesOutboundPacket);

                LOG.info(
                        "Player left - token: {}, user id: {}, ply id: {}",
                        playerInfo.session.getToken(),
                        user != null ? user.getUserId() : null,
                        playerInfo.playerId
                );
            }
        }
        else
        {
            LOG.warn("Attempted to unregister unknown player - socket addr: {}", ws.getRemoteSocketAddress());
        }
    }

    @Override
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

    public synchronized void playerSpawnAndSendData(PlayerInfo playerInfo) throws IOException
    {
        // Determine lobby/default map for player
        WorldMap map = mapService.mainMap;

        // Create entity for player
        Entity entityPlayer = playerEntityService.createPlayer(map, playerInfo);

        // Spawn the player
        map.respawnManager.respawn(new EntityPendingRespawn(controller, entityPlayer));

        // Send map data
        packetService.send(playerInfo, map.getPacket());

        // Send update of entire world to the player
        EntityUpdatesOutboundPacket packetUpdates = new EntityUpdatesOutboundPacket();
        packetUpdates.build(map.entityManager, true);
        controller.packetService.send(playerInfo, packetUpdates);

        // Inform server player has joined
        PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
        playerEventsUpdatesOutboundPacket.writePlayerJoined(playerInfo);
        broadcast(playerEventsUpdatesOutboundPacket);
    }

    public synchronized void setPlayerEnt(PlayerInfo playerInfo, Entity entity)
    {
        Entity currentEntity = playerInfo.entity;

        // Persist the player's current entity
        playerEntityService.persistPlayer(playerInfo);

        if (currentEntity != null && currentEntity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) currentEntity;

            // Remove player from old entity
            playerEntity.removePlayer(playerInfo);

            // Check if to remove old entity...
            if (playerEntity.isRemovableOnPlayerEntChange(playerInfo))
            {
                entity.map.entityManager.remove(currentEntity);
            }
        }

        // Update entity
        playerInfo.entity = entity;

        // Create packet to update ID for clientside
        try
        {
            PlayerIdentityOutboundPacket packet = new PlayerIdentityOutboundPacket();
            packet.writeIdentity(playerInfo);
            controller.packetService.send(playerInfo, packet);
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
        PlayerInfo playerInfo;
        for (Map.Entry<WebSocket, PlayerInfo> kv : mappings.entrySet())
        {
            playerInfo = kv.getValue();
            controller.packetService.send(playerInfo, outboundPacket);
        }
    }

    /**
     * Sends a packet to all players in a map.
     *
     * @param outboundPacket
     * @param map
     */
    public synchronized void broadcast(OutboundPacket outboundPacket, WorldMap map)
    {
        PlayerInfo playerInfo;
        Entity entity;

        for (Map.Entry<WebSocket, PlayerInfo> kv : mappings.entrySet())
        {
            playerInfo = kv.getValue();

            if (playerInfo.getCurrentMap(mapService) == map)
            {
                controller.packetService.send(playerInfo, outboundPacket);
            }
        }
    }
    
}
