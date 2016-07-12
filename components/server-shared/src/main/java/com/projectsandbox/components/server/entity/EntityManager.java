package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.entity.death.MapBoundsKiller;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.network.packet.imp.entity.EntityUpdatesOutboundPacket;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.service.EventLogicCycleService;
import com.projectsandbox.components.server.util.IdCounterProvider;
import com.projectsandbox.components.server.util.counters.IdCounterConsumer;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles all of the entities in the world.
 *
 * Thread-safe.
 */
public class EntityManager implements EventLogicCycleService, IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    private Controller controller;
    private WorldMap map;

    /* Used for testing collision detection.  */
    @Autowired
    private CollisionDetection collisionDetection;

    /* Used for converting entity types to classes. */
    @Autowired
    private EntityTypeMappingStoreService entityTypeMappingStoreService;

    /* Used for efficient collision detection and network updates. */
    private QuadTree quadTree;

    /* A map of entity id -> entity. */
    private final Map<Short, Entity> entities;

    /* Counter for producing entity identifiers. */
    private IdCounterProvider idCounterProvider;


    public EntityManager(Controller controller, WorldMap map)
    {
        this.controller = controller;
        this.map = map;

        // Setup collections...
        this.entities = new ConcurrentHashMap<>();
        this.idCounterProvider = new IdCounterProvider(this);

        // Inject dependencies...
        controller.inject(this);
    }

    public synchronized void postMapLoad()
    {
        this.quadTree = new QuadTree(map);
    }

    public synchronized Entity fetch(Short key)
    {
        return entities.get(key);
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
        // TODO: consider removal of sync (entity), may be too excessive...
        synchronized (entity)
        {
            synchronized (this)
            {
                // Update state to created - for update to all players!
                entity.setState(EntityState.CREATED);

                // Add entity to pending map
                entities.put(entityId, entity);

                // Add to quadtree
                quadTree.update(entity);

                LOG.debug("Added entity to world - entity: {}", entity);
            }
        }

        return true;
    }

    @Override
    public synchronized boolean containsId(short id)
    {
        return entities.containsKey(id);
    }

    public synchronized void remove(Entity entity)
    {
        synchronized (entity)
        {
            if (entity.id != null && entities.containsKey(entity.id))
            {
                // Mark entity for deletion
                entity.setState(EntityState.PENDING_DELETED);

                LOG.debug("Entity set for removal - ent id: {}", entity.id);

                // Invoke entity event handler
                entity.eventPendingDeleted(controller);
            }
        }
    }

    @Override
    public void logic()
    {
        try
        {
            // Execute logic for each entity
            executeEntityLogic();

            // Perform collision detection
            performCollisionDetection();

            // Build and distribute update packets
            sendEntityUpdatesToPlayers();

            // Update state of entities
            updateEntityStates();
        }
        catch (Exception e)
        {
            LOG.error("Exception during entity-manager logic", e);
        }
    }

    public QuadTree getQuadTree()
    {
        return quadTree;
    }

    public Map<Short, Entity> getEntities()
    {
        return entities;
    }

    private void executeEntityLogic()
    {
        synchronized (entities)
        {
            for (Entity entity : entities.values())
            {
                // We won't run logic for deleted or dead enities
                if (!entity.isDeleted())
                {
                    entity.eventLogic(controller);
                }
            }
        }
    }

    private void performCollisionDetection()
    {
        // Fetch map boundaries
        float mapMaxX = map.getMaxX();
        float mapMaxY = map.getMaxY();

        // Perform collision check for each entity
        synchronized (entities)
        {
            for (Entity entityA : entities.values())
            {
                performCollisionDetection(mapMaxX, mapMaxY, entityA);
            }
        }
    }

    private void performCollisionDetection(float mapMaxX, float mapMaxY, Entity entity)
    {
        // Check entity is not deleted or dead
        if (!entity.isDeleted())
        {
            performCollisionDetectionEntities(entity);

            // Check entity has still not been deleted or dead
            if (!entity.isDeleted())
            {
                performCollisionDetectionMap(mapMaxX, mapMaxY, entity);
            }
        }
    }

    private void performCollisionDetectionEntities(Entity entityA)
    {
        // Fetch potential entities from quad-tree
        Set<Entity> nearbyEntities = quadTree.getCollidableEntities(entityA);
        CollisionResult collisionResult;

        // Perform collision detection/handling with other entities
        for (Entity entityB : nearbyEntities)
        {
            // Check next entity is not: dead, deleted or the same entity
            if (!entityB.isDeleted() && entityA.id != entityB.id)
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
                    if (entityA.isDeleted())
                    {
                        break;
                    }
                }
            }
        }
    }

    private void performCollisionDetectionMap(float mapMaxX, float mapMaxY, Entity entity)
    {
        // Perform collision with map
        Collection<CollisionMapResult> mapResults = collisionDetection.collisionMap(entity);

        for (CollisionMapResult mapResult : mapResults)
        {
            entity.eventCollisionMap(controller, mapResult);
        }

        // Check ent is not outside map
        if (!entity.isDeleted() &&
                (
                        entity.positionNew.x < 0.0f || entity.positionNew.y < 0.0f ||
                                entity.positionNew.x > mapMaxX || entity.positionNew.y > mapMaxY
                )
            )
        {
            LOG.warn("Entity went outside the map - ent: {}, pos: {}", entity, entity.positionNew);

            // Kill the ent...
            entity.kill(controller, null, MapBoundsKiller.class);
        }
        else
        {
            // Update position for entity
            entity.position.copy(entity.positionNew);
        }
    }

    private void sendEntityUpdatesToPlayers()
    {
        // Iterate each player and provide updates within radius
        EntityUpdatesOutboundPacket packet;

        Set<PlayerInfo> players = controller.playerService.getPlayers();

        synchronized (players)
        {
            for (PlayerInfo playerInfo : players)
            {
                try
                {
                    // Build updates packet
                    packet = new EntityUpdatesOutboundPacket();
                    packet.build(this, playerInfo);

                    // Send updates
                    controller.packetService.send(playerInfo, packet);
                }
                catch (IOException e)
                {
                    LOG.error("Failed to build entity updates packet for player - player id: {}", playerInfo.playerId, e);
                }
            }
        }
    }

    private void updateEntityStates()
    {
        synchronized (entities)
        {
            // Iterate each entity and transition their state
            Iterator<Map.Entry<Short, Entity>> iterator = entities.entrySet().iterator();

            Map.Entry<Short, Entity> kv;
            Entity entity;

            while (iterator.hasNext())
            {
                kv = iterator.next();
                entity = kv.getValue();

                // Remove deleted entities, else transition to next state...
                if (entity.getState() == EntityState.DELETED)
                {
                    Short entityId = entity.id;

                    entity.id = null;
                    iterator.remove();

                    LOG.info("deleted entity - id: {}", entityId);
                }
                else
                {
                    entity.transitionState();
                }
            }
        }
    }

    public CollisionDetection getCollisionDetection()
    {
        return collisionDetection;
    }

}
