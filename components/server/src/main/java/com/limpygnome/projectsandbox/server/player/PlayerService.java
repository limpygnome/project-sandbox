    
package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntityService;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.network.Socket;
import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.PacketService;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.global.PlayerEventsUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.network.packet.imp.player.individual.PlayerIdentityOutboundPacket;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import java.io.IOException;
import java.util.*;

import com.limpygnome.projectsandbox.server.util.IdCounterProvider;
import com.limpygnome.projectsandbox.server.util.counters.IdCounterConsumer;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final IdCounterProvider idCounterProvider;
    private final Map<Socket, PlayerInfo> mappings;
    private final Map<Short, PlayerInfo> mappingsById;
    private final Set<UUID> connectedRegisteredPlayers;
    private final List<PlayerInfo> players;

    public PlayerService()
    {
        this.mappings = new HashMap<>();
        this.mappingsById = new HashMap<>();
        this.connectedRegisteredPlayers = new HashSet<>();
        this.idCounterProvider = new IdCounterProvider(this);
        this.players = new LinkedList<>();
    }

    /**
     * Attempts to register a new player.
     *
     * @param socket the socket
     * @param session the game session
     * @return an instance, or null if the player cannot be registered.
     */
    public PlayerInfo register(Socket socket, GameSession session)
    {
        try
        {
            // Check a registered user is not already connected
            synchronized (this)
            {
                User user = session.getUser();

                if (user != null && connectedRegisteredPlayers.contains(user.getUserId()))
                {
                    // TODO: this needs to throw an exception/send back data, detailing why the user cannot connect
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
            PlayerInfo playerInfo = new PlayerInfo(socket, session, playerId);

            synchronized (playerInfo)
            {
                synchronized (this)
                {
                    // Add mapping for sock
                    mappings.put(socket, playerInfo);

                    // Add mapping for identifier
                    mappingsById.put(playerId, playerInfo);

                    // Add to list of players
                    synchronized (players)
                    {
                        players.add(playerInfo);
                    }

                    LOG.info("Player joined - ply id: {}, name: {}", playerId, session.getNickname());
                }

                // Give the user all of the users and metrics/stats thus far
                PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundSnapshotPacket = new PlayerEventsUpdatesOutboundPacket();
                writePlayersJoined(playerEventsUpdatesOutboundSnapshotPacket);
                writePlayerMetrics(playerEventsUpdatesOutboundSnapshotPacket, true);
                controller.packetService.send(playerInfo, playerEventsUpdatesOutboundSnapshotPacket);

                // Create, spawn and send data for player
                playerSpawnAndSendData(playerInfo);

                // Send previous chat messages
                controller.chatService.sendPreviousMessages(playerInfo);

                LOG.info(
                        "Player joined - session token: {}, user id: {}, ply id: {}",
                        session.getToken(),
                        session.getUser() != null ? session.getUser().getUserId() : null,
                        playerId
                );
            }

            return playerInfo;
        }
        catch(IOException e)
        {
            LOG.error("Failed to register player", e);
            return null;
        }
    }
    public void unregister(Socket ws)
    {
        // Fetch associated player, and entity
        PlayerInfo playerInfo;

        synchronized (this)
        {
            playerInfo = mappings.get(ws);
        }

        if (playerInfo != null)
        {
            synchronized (playerInfo)
            {
                Entity entity = playerInfo.entity;

                // Persist player's current entity
                playerEntityService.persistPlayer(playerInfo);

                // Remove entity
                // TODO: make this more generic...perhaps remove if spawned entity
                if (entity != null && entity instanceof PlayerEntity)
                {
                    PlayerEntity playerEntity = (PlayerEntity) entity;

                    if (playerEntity.isRemovableOnPlayerEntChange(playerInfo))
                    {
                        entity.map.entityManager.remove(playerEntity);
                        LOG.debug("Removed entity for disconnecting player - ply id: {}, ent id: {}", playerInfo.playerId, entity.id);
                    }
                }

                User user;

                synchronized (this)
                {
                    // Remove socket mapping
                    mappings.remove(ws);

                    // Remove ID mapping
                    mappingsById.remove(playerInfo.playerId);

                    // Remove from players
                    synchronized (players)
                    {
                        players.remove(playerInfo);
                    }

                    // Remove from connected registered players (if registered)
                    user = playerInfo.session.getUser();

                    if (user != null)
                    {
                        connectedRegisteredPlayers.remove(user.getUserId());
                    }
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

    public List<PlayerInfo> getPlayers()
    {
        return this.players;
    }

    private void playerSpawnAndSendData(PlayerInfo playerInfo) throws IOException
    {
        synchronized (playerInfo)
        {
            // Determine lobby/default map for player
            WorldMap map = mapService.mainMap;

            // Create entity for player
            Entity entityPlayer = playerEntityService.createPlayer(map, playerInfo);

            // Spawn the player
            map.respawnManager.respawn(new EntityPendingRespawn(controller, entityPlayer));

            // Send map data
            packetService.send(playerInfo, map.getPacket());

            // Inform everyone else that player has joined
            PlayerEventsUpdatesOutboundPacket playerEventsUpdatesOutboundPacket = new PlayerEventsUpdatesOutboundPacket();
            playerEventsUpdatesOutboundPacket.writePlayerJoined(playerInfo);
            broadcast(playerEventsUpdatesOutboundPacket);
        }
    }

    public void setPlayerEnt(PlayerInfo playerInfo, Entity entity)
    {
        synchronized (playerInfo)
        {
            Entity currentEntity = playerInfo.entity;

            if (entity != currentEntity)
            {
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
            }

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
    }

    public PlayerInfo getPlayerBySocket(Socket socket)
    {
        return mappings.get(socket);
    }

    public synchronized void broadcast(OutboundPacket outboundPacket)
    {
        PlayerInfo playerInfo;
        for (Map.Entry<Socket, PlayerInfo> kv : mappings.entrySet())
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

        for (Map.Entry<Socket, PlayerInfo> kv : mappings.entrySet())
        {
            playerInfo = kv.getValue();

            if (playerInfo.getCurrentMap(mapService) == map)
            {
                controller.packetService.send(playerInfo, outboundPacket);
            }
        }
    }
    
}
