package com.projectsandbox.components.server.entity.respawn;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.service.EventLogicCycleService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.FactionSpawns;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A layer above {@link EntityManager} for respawning an entity with additional params.
 *
 * TODO: add support for multiple maps; this should be a single instance for all maps
 */
public class RespawnManager implements EventLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(RespawnManager.class);

    private Controller controller;

    private WorldMap map;
    private LinkedList<PendingRespawn> pendingRespawnList;

    /* Faction ID -> FactionSpawns */
    private HashMap<Short, FactionSpawns> factionSpawnsMap;

    public RespawnManager(Controller controller, WorldMap map)
    {
        this.controller = controller;
        this.map = map;
        this.pendingRespawnList = new LinkedList<>();
        this.factionSpawnsMap = new HashMap<>();
    }

    public synchronized void factionSpawnsAdd(Short mapId, FactionSpawns factionSpawns)
    {
        this.factionSpawnsMap.put(factionSpawns.getFactionId(), factionSpawns);
        LOG.debug("Added faction spawns - map id: {}, spawns: {}", mapId, factionSpawns);
    }

    public synchronized FactionSpawns factionSpawnsGet(short factionId)
    {
        return this.factionSpawnsMap.get(factionId);
    }

    public void respawn(PendingRespawn pendingRespawn)
    {
        /*
            WARNING: do not put synchronize on this method. It makes a call out to EntityManager,
            which can cause a deadlock scenario. EntityManager could be in a logic loop and attempt
            to contact respawn manager. Since it has its own synchronize around the logic loop, this manager attempts
            to access one of its other synchronize methods, causing a deadlock.
         */

        // Handle entity state transition based on current state
        Entity entity = pendingRespawn.entity;

        // Remove from world, if already exists
        map.entityManager.remove(entity);

        // Add to pending respawn...
        addToInternalPendingRespawnCollectionSynchronized(pendingRespawn);

        LOG.debug("Entity added for respawn - ent id: {}, respawn time: {}, current time: {}",
                pendingRespawn.entity.id, pendingRespawn.gameTimeRespawn, controller.gameTime()
        );
    }

    private synchronized void addToInternalPendingRespawnCollectionSynchronized(PendingRespawn pendingRespawn)
    {
        // Add at suitable index based on time to respawn, so that items with shortest time are at the front
        // for efficiency (saves having to iterate the entire list, can just check top)
        Iterator<PendingRespawn> iterator = pendingRespawnList.iterator();
        PendingRespawn pendingRespawnItem;
        int index = 0;

        while (iterator.hasNext())
        {
            pendingRespawnItem = iterator.next();

            if (pendingRespawnItem.gameTimeRespawn > pendingRespawn.gameTimeRespawn)
            {
                // We have found the index to insert our item
                break;
            }
            else
            {
                index++;
            }
        }

        pendingRespawnList.add(index, pendingRespawn);
    }

    @Override
    public void logic()
    {
        LinkedList<RespawnData> entitiesToSpawn = new LinkedList<>();

        // Check if next entity can respawn yet; if it can, add it to a list of ents to be added to ent manager
        synchronized (this)
        {
            RespawnData respawnData;
            PendingRespawn pendingRespawn;
            Iterator<PendingRespawn> iterator = pendingRespawnList.iterator();

            long gameTime = controller.gameTime();

            while (iterator.hasNext())
            {
                pendingRespawn = iterator.next();

                // TODO: consider timeouts, we could have blocked spawns and a lot of CPU usage here...

                if (gameTime >= pendingRespawn.gameTimeRespawn)
                {
                    respawnData = findSpawn(pendingRespawn);

                    if (respawnData != null)
                    {
                        iterator.remove();
                        entitiesToSpawn.add(respawnData);
                    }
                    else
                    {
                        LOG.warn("Could not respawn entity - ent id: {}", pendingRespawn.entity.id);
                    }
                }
            }
        }

        // Add entities to world
        for (RespawnData respawnData : entitiesToSpawn)
        {
            if (!respawnEntity(respawnData))
            {
                // Re-queue if failed...
                addToInternalPendingRespawnCollectionSynchronized(respawnData.pendingRespawn);
            }
        }
    }

    private RespawnData findSpawn(PendingRespawn pendingRespawn)
    {
        Entity entity = pendingRespawn.entity;

        // Fetch spawn position
        Spawn spawn = pendingRespawn.getSpawnPosition(controller);

        if (spawn == null)
        {
            LOG.debug("No spawn available - entity id: {}, faction id: {}", entity.id, entity.faction);
            return null;
        }

        return new RespawnData(spawn, pendingRespawn);
    }

    private boolean respawnEntity(RespawnData respawnData)
    {
        Entity entity = respawnData.pendingRespawn.entity;
        PlayerEntity playerEntity = null;

        // Determine if entity has been respawned from persistence
        boolean respawnAfterPersisted = false;

        if (entity instanceof PlayerEntity)
        {
            playerEntity = (PlayerEntity) entity;

            if (playerEntity.isRespawnPersistedPlayer())
            {
                respawnAfterPersisted = true;
            }
        }

        // Reset entity
        entity.eventReset(controller, respawnData.spawn, respawnAfterPersisted);

        // Add to world if new entity
        if (!map.entityManager.add(entity))
        {
            LOG.warn("Could not respawn entity, failed to add to entity manager - entity id: {}", entity.id);
            return false;
        }

        // Reset respwan-persisted flag
        if (respawnAfterPersisted)
        {
            playerEntity.setRespawnPersistedPlayer(false);
        }

        // Invoke spawn event
        entity.eventSpawn(controller, respawnData.spawn);

        LOG.debug("Spawned entity - entity: {} - spawn: {}", entity, respawnData.spawn);

        return true;
    }

}