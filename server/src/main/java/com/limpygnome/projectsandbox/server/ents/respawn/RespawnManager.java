package com.limpygnome.projectsandbox.server.ents.respawn;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.EntityManager;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
import com.limpygnome.projectsandbox.server.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.server.world.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A layer above {@link EntityManager} for respawning an entity with additional params.
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
    }

    public synchronized void addFactionSpawns(short mapId, FactionSpawns factionSpawns)
    {
        this.factionSpawnsMap.put(factionSpawns.getFactionId(), factionSpawns);
    }

    public synchronized void respawn(PendingRespawn pendingRespawn)
    {
        // Ensure entity has been removed from world
        controller.entityManager.remove(pendingRespawn.entity);

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

        LOG.debug("Entity added for respawn - ent id: {}, index: {}", pendingRespawn.entity.id, index);
    }

    public synchronized void logic()
    {
        // Check if next entity can respawn yet
        PendingRespawn pendingRespawn;
        Iterator<PendingRespawn> iterator = pendingRespawnList.iterator();
        Entity entity;

        while (iterator.hasNext() && (pendingRespawn = iterator.next()).gameTimeRespawn <= controller.gameTime())
        {
            entity = pendingRespawn.entity;

            // Set state to created
            entity.setState(StateChange.CREATED);

            // Attempt to spawn the entity
            if (findEntitySpawnPosition(entity))
            {
                // Attempt to add the entity through the entity manager (re-adding it to the world)
                if (controller.entityManager.add(entity))
                {
                    // Remove from our spawn manager
                    iterator.remove();
                }
                else
                {
                    LOG.warn("Unable to add entity to entity manager during respawn - ent id: " + entity.id);
                }
            }
        }
    }

    private boolean findEntitySpawnPosition(Entity entity)
    {
        // Fetch spawn for factionSpawns
        FactionSpawns factionSpawns = factionSpawnsMap.get(entity.faction);

        // Check ent for its own custom spawn
        if (entity.spawn != null)
        {
            entityRespawnSetup(entity, entity.spawn);
            return true;
        }
        // Check we have a factionSpawns to fetch factionSpawns spawns
        else if (factionSpawns == null)
        {
            LOG.warn("Cannot find factionSpawns for entity - id: {}, factionSpawns: {}", entity.id, entity.faction);
            entity.setState(StateChange.PENDING_DELETED);
            return false;
        }
        // Use factionSpawns spawn
        else if (factionSpawns.hasSpawns())
        {
            Spawn spawn = factionSpawns.getNextSpawn();
            entityRespawnSetup(entity, spawn);
            return true;
        }
        else
        {
            LOG.warn("No spawns available for factionSpawns - id: {}, factionSpawns: {}", entity.id, entity.faction);
            entity.setState(StateChange.PENDING_DELETED);
            return false;
        }
    }

    private synchronized void entityRespawnSetup(Entity ent, Spawn spawn)
    {
        // Setup entity for its new life
        ent.reset();

        // Set position etc for spawn
        ent.positionNew.x = spawn.x;
        ent.positionNew.y = spawn.y;
        ent.position.copy(ent.positionNew);
        ent.rotation = spawn.rotation;
        ent.updateMask(UpdateMasks.ALL_MASKS);

        // Rebuild vertices
        ent.rebuildCachedVertices();

        // Inform the ent it has been spawned
        ent.eventSpawn();

        LOG.debug("Spawned entity - entity: {} - spawn: {}", ent, spawn);
    }

}
