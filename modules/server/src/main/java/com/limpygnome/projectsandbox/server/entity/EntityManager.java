package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.entity.death.MapBoundsKiller;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionDetection;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.network.packet.imp.entity.EntityUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.service.EventLogicCycleService;
import com.limpygnome.projectsandbox.server.util.IdCounterProvider;
import com.limpygnome.projectsandbox.server.util.counters.IdCounterConsumer;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles all of the entities in the world.
 *
 * Notes:
 * - Any modifications to the internal collections should always synchronize on `entities`.
 */
public class EntityManager implements EventLogicCycleService, IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    private Controller controller;
    private WorldMap map;

    @Autowired
    private CollisionDetection collisionDetection;

    @Autowired
    public EntTypeMappingStoreService entTypeMappingStoreService;

    public final HashMap<Short, Entity> entities;
    private final HashMap<Short, Entity> entitiesNew;
    private IdCounterProvider idCounterProvider;

    public EntityManager(Controller controller, WorldMap map)
    {
        this.controller = controller;
        this.map = map;

        // Setup collections...
        this.entities = new HashMap<>();
        this.entitiesNew = new HashMap<>();
        this.idCounterProvider = new IdCounterProvider(this);

        // Inject dependencies...
        controller.inject(this);
    }

    public Entity fetch(Short key)
    {
        synchronized (this)
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
    public boolean add(Entity entity)
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
        synchronized (this)
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
        synchronized (this)
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
        boolean result = false;

        synchronized (this)
        {
            // Check ents collection
            Entity entityFetchedWorld = entities.get(entityId);

            if (entityFetchedWorld != null && entityFetchedWorld == entity)
            {
                // Update entity and call events
                entity.setState(EntityState.PENDING_DELETED);

                LOG.debug("Entity set for removal - {}", entity);

                result = true;
            }
            else
            {
                // Attempt removal on ents to be added - unlikely, but still possible
                Entity entityFetchedNew = entitiesNew.get(entityId);

                if (entityFetchedNew != null && entityFetchedNew == entity)
                {
                    LOG.debug("Newly added entity set for removal- {}", entity);
                    result = true;
                }
            }
        }

        if (result)
        {
            // Invoke event for entity
            entity.eventPendingDeleted(controller);
        }

        return result;
    }

    @Override
    public void logic()
    {
        try
        {
            EntityUpdatesOutboundPacket entityUpdatesOutboundPacket;

            synchronized (this)
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
                        entityA.eventLogic(controller);
                    }
                }

                // Fetch map boundaries
                float mapMaxX = map.getMaxX();
                float mapMaxY = map.getMaxY();

                // Perform collision check for each entity
                CollisionResult collisionResult;
                Collection<CollisionResultMap> mapResults;

                for (Map.Entry<Short, Entity> kv : entities.entrySet())
                {
                    entityA = kv.getValue();

                    // Check entity is not deleted or dead
                    if (!entityA.isDeleted() && !entityA.isDead())
                    {
                        // TODO: CRITICAL - upgrade with quadtree, N^2 - really bad...

                        // Perform collision detection/handling with other ents
                        for (Map.Entry<Short, Entity> kv2 : entities.entrySet())
                        {
                            entityB = kv2.getValue();

                            // Check next entity is not: dead, deleted or the same ent
                            if (!entityB.isDeleted() && !entityB.isDead() &&  entityA.id != entityB.id)
                            {
                                // Perform collision detection
                                collisionResult = collisionDetection.collision(entityA, entityB);

                                // Check if a collision occurred
                                if (collisionResult.collision)
                                {
                                    // Inform both ents of event
                                    entityA.eventCollisionEntity(controller, entityB, entityA, entityB, collisionResult);
                                    entityB.eventCollisionEntity(controller, entityB, entityA, entityA, collisionResult);

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
                            mapResults = collisionDetection.collisionMap(entityA);

                            for (CollisionResultMap mapResult : mapResults)
                            {
                                entityA.eventCollisionMap(controller, mapResult);
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
                                LOG.warn("Entity went outside the map - ent: {}, pos: {}", entityA, entityA.positionNew);

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
                entityUpdatesOutboundPacket = new EntityUpdatesOutboundPacket();
                entityUpdatesOutboundPacket.build(this, false);
            }

            // Send updates to players in map
            controller.playerService.broadcast(entityUpdatesOutboundPacket, map);
        }
        catch (Exception e)
        {
            LOG.error("Exception during logic", e);
        }
    }

}
