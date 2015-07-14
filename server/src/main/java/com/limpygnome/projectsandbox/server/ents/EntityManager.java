package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.ents.death.MapBoundsKiller;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.EntityState;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.SAT;
import com.limpygnome.projectsandbox.server.packets.types.ents.EntityUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.utils.IdCounterProvider;
import com.limpygnome.projectsandbox.server.utils.counters.IdCounterConsumer;
import com.limpygnome.projectsandbox.server.world.EntTypeMappings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles all of the entities in the world.
 *
 * Notes:
 * - Any modifications to the internal collections should always synchronize on `entities`.
 */
public class EntityManager implements IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    private final Controller controller;
    public final HashMap<Short, Entity> entities;
    private final HashMap<Short, Entity> entitiesNew;
    public EntTypeMappings entTypeMappings;
    private IdCounterProvider idCounterProvider;

    public EntityManager(Controller controller)
    {
        this.controller = controller;
        this.entities = new HashMap<>();
        this.entitiesNew = new HashMap<>();
        this.entTypeMappings = new EntTypeMappings();
        this.idCounterProvider = new IdCounterProvider(this);
    }

    public Entity fetch(Short key)
    {
        synchronized (entities)
        {
            return entities.get(key);
        }
    }

    /**
     * Entities should not be added this way, only through the respawn manager.
     *
     * @param entity
     * @return
     */
    protected boolean add(Entity entity)
    {
        // Fetch the next available identifier
        Short entityId = idCounterProvider.nextId(entity.id);

        // Check we found an identifier
        if (entityId == null)
        {
            LOG.error("Unable to create identifier for entity - {}", entity);
            return false;
        }

        // Assign id to entity
        entity.id = entityId;

        // Add mapping
        synchronized (entities)
        {
            // Add entity to pending map
            entitiesNew.put(entityId, entity);

            // Update state to created - for update to all players!
            entity.setState(EntityState.CREATED);

            LOG.debug("Entity pending addition to the world - {}", entity);
        }

        return true;
    }

    @Override
    public boolean containsId(short id)
    {
        synchronized (entities)
        {
            return entities.containsKey(id) || entitiesNew.containsKey(id);
        }
    }

    public boolean remove(short entityId)
    {
        return removeInternal(entityId, null);
    }

    public boolean remove(Entity entity)
    {
        return removeInternal(entity.id, entity);
    }

    private boolean removeInternal(short entityId, Entity entity)
    {
        synchronized (entities)
        {
            // Check ents collection
            Entity entityFetchedWorld = entities.get(entityId);

            if (entityFetchedWorld != null && entityFetchedWorld == entity)
            {
                // Update entity and call events
                entity.setState(EntityState.PENDING_DELETED);
                entity.eventPendingDeleted(controller);

                LOG.debug("Entity set for removal - {}", entity);

                return true;
            }

            // Attempt removal on ents to be added - unlikely, but still possible
            Entity entityFetchedNew = entitiesNew.get(entityId);

            if (entityFetchedNew != null && entityFetchedNew == entity)
            {

                    LOG.debug("Newly added entity set for removal- {}", entity);
                    return true;
            }
        }

        return false;
    }

    public void logic()
    {
        try
        {
            synchronized (entities)
            {
                Entity entityA;
                Entity entityB;

                // Execute logic for each entity
                for (Map.Entry<Short, Entity> kv : entities.entrySet())
                {
                    entityA = kv.getValue();

                    // We won't run logic for deleted or dead enities
                    if (!entityA.isDeleted() && !entityA.isDead())
                    {
                        entityA.logic(controller);
                    }
                }

                // Fetch map boundaries
                // TODO: update if we have multiple maps
                float mapMaxX = controller.mapManager.main.maxX;
                float mapMaxY = controller.mapManager.main.maxY;

                // Perform collision check for each entity
                CollisionResult collisionResult;
                Collection<CollisionResultMap> mapResults;

                for (Map.Entry<Short, Entity> kv : entities.entrySet())
                {
                    entityA = kv.getValue();

                    // Check entity is not deleted or dead
                    if (!entityA.isDeleted() && !entityA.isDead())
                    {
                        // TODO: upgrade with quadtree, N^N - really bad...

                        // Perform collision detection/handling with other ents
                        for (Map.Entry<Short, Entity> kv2 : entities.entrySet())
                        {
                            entityB = kv2.getValue();

                            // Check next entity is not: dead, deleted or the same ent
                            if (!entityB.isDeleted() && !entityB.isDead() &&  entityA.id != entityB.id)
                            {
                                // Perform collision detection
                                collisionResult = SAT.collision(entityA, entityB);

                                // Check if a collision occurred
                                if (collisionResult.collision)
                                {
                                    // Inform both ents of event
                                    entityA.eventHandleCollision(controller, entityB, entityA, entityB, collisionResult);
                                    entityB.eventHandleCollision(controller, entityB, entityA, entityA, collisionResult);

                                    // Check if our original entity is now deleted
                                    // -- Only the two above events should be able to kill it
                                    if (entityA.isDeleted() || entityA.isDead())
                                    {
                                        break;
                                    }
                                }
                            }
                        }

                        // Check entity has still not been deleted or dead
                        if (!entityA.isDeleted() && !entityA.isDead())
                        {
                            // Perform collision with map
                            // TODO: add support for multiple maps
                            mapResults = SAT.collisionMap(controller.mapManager.main, entityA);

                            for (CollisionResultMap mapResult : mapResults)
                            {
                                entityA.eventHandleCollisionMap(controller, mapResult);
                            }

                            // Update position for ent
                            entityA.position.copy(entityA.positionNew);

                            // Check ent is not outside map
                            if  (!entityA.isDeleted() && !entityA.isDead() &&
                                    (
                                            entityA.positionNew.x < 0.0f || entityA.positionNew.y < 0.0f ||
                                                    entityA.positionNew.x > mapMaxX || entityA.positionNew.y > mapMaxY
                                    )
                                    )
                            {
                                // Kill the ent...
                                entityA.kill(controller, null, MapBoundsKiller.class);
                            }
                        }
                    }

                }

                // Add pending ents
                if (!entitiesNew.isEmpty())
                {
                    // Iterate new ent and add to world
                    Entity entity;

                    for (Map.Entry<Short, Entity> kv : entitiesNew.entrySet())
                    {
                        entity = kv.getValue();

                        // Check ent has not been deleted
                        if (!entity.isDeleted())
                        {
                            // Add to world
                            entities.put(entity.id, entity);

                            LOG.debug("Added entity to world - entity: {}", entity);
                        }
                    }

                    entitiesNew.clear();
                }

                // Build update packet
                EntityUpdatesOutboundPacket packet = new EntityUpdatesOutboundPacket();
                packet.build(controller.entityManager, false);

                // Send updates to all players
                controller.playerManager.broadcast(packet);
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception during logic", e);
        }
    }

}
