package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.EntityState;
import com.limpygnome.projectsandbox.server.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.server.ents.respawn.PendingRespawn;
import com.limpygnome.projectsandbox.server.world.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.Spawn;
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
public class RespawnManager
{
    private final static Logger LOG = LogManager.getLogger(RespawnManager.class);

    private Controller controller;
    private LinkedList<PendingRespawn> pendingRespawnList;
    private HashMap<Short, FactionSpawns> factionSpawnsMap;

    public RespawnManager(Controller controller)
    {
        this.controller = controller;
        this.pendingRespawnList = new LinkedList<>();
        this.factionSpawnsMap = new HashMap<>();
    }

    public synchronized void factionSpawnsAdd(short mapId, FactionSpawns factionSpawns)
    {
        this.factionSpawnsMap.put(factionSpawns.getFactionId(), factionSpawns);
        LOG.debug("Added faction spawns - map id: {}, spawns: {}", mapId, factionSpawns);
    }

    public synchronized FactionSpawns factionSpawnsGet(short mapId, short factionId)
    {
        return this.factionSpawnsMap.get(factionId);
    }

    public synchronized void respawn(PendingRespawn pendingRespawn)
    {
        // Handle entity state transition based on current state
        Entity entity = pendingRespawn.entity;

        switch (entity.getState())
        {
            case CREATED:
                // Ensure entity does not already exist
                controller.entityManager.remove(entity);
                break;
            case NONE:
            case UPDATED:
                // Do nothing, basically just allowed states...
                break;
            default:
                throw new RuntimeException("Invalid state for entity respawn");
        }

        // Add at suitable index based on time to respawn
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

        LOG.debug("Entity added for respawn - ent id: {}, index: {}, respawn time: {}, current time: {}",
                pendingRespawn.entity.id, index, pendingRespawn.gameTimeRespawn, controller.gameTime()
        );
    }

    public synchronized void logic()
    {
        // Check if next entity can respawn yet
        PendingRespawn pendingRespawn;
        Iterator<PendingRespawn> iterator = pendingRespawnList.iterator();

        while (iterator.hasNext() && (pendingRespawn = iterator.next()).gameTimeRespawn <= controller.gameTime())
        {
            // TODO: consider timeouts, we could have blocked spawns and a lot of CPU usage here...
            if (respawnEntity(pendingRespawn))
            {
                iterator.remove();
            }
        }
    }

    private boolean respawnEntity(PendingRespawn pendingRespawn)
    {
        Entity entity = pendingRespawn.entity;

        // Fetch spawn position
        Spawn spawn = pendingRespawn.getSpawnPosition(controller);

        if (spawn == null)
        {
            LOG.debug("No spawn available - entity id: {}, faction id: {}", entity.id, entity.faction);
            return false;
        }

        // Set position etc for spawn
        entity.positionNew.x = spawn.x;
        entity.positionNew.y = spawn.y;
        entity.position.copy(entity.positionNew);
        entity.rotation = spawn.rotation;
        entity.updateMask(UpdateMasks.ALL_MASKS);

        // Setup entity for its new life
        entity.eventReset(controller);

        // Rebuild vertices
        entity.rebuildCachedVertices();

        // Add to world if new entity
        if (entity.getState() == EntityState.CREATED && !controller.entityManager.add(entity))
        {
            LOG.warn("Could not respawn entity, failed to add to entity manager - entity id: {}, spawn: {}", entity.id, spawn);
            return false;
        }

        // Invoke spawn event
        entity.eventSpawn(controller);

        LOG.debug("Spawned entity - entity: {} - spawn: {}", entity, spawn);

        return true;
    }

}
