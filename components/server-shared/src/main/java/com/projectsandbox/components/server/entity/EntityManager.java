package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.entity.death.MapBoundsKiller;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.network.packet.imp.entity.EntityUpdatesOutboundPacket;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.service.EventMapLogicCycleService;
import com.projectsandbox.components.server.util.IdCounterProvider;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles all of the entities in the world.
 *
 * Thread-safe.
 */
@Component
public class EntityManager implements EventMapLogicCycleService
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    @Autowired
    private Controller controller;

    /* Used for testing collision detection.  */
    @Autowired
    private CollisionDetection collisionDetection;

    public boolean add(Entity entity)
    {
        WorldMap map = entity.map;
        EntityMapData mapData = map.getEntityMapData();
        return mapData.add(entity);
    }

    public void remove(Entity entity)
    {
        WorldMap map = entity.map;
        EntityMapData mapData = map.getEntityMapData();

        // Remove from entities
        mapData.remove(controller, entity);
    }

    @Override
    public void logic(WorldMap map)
    {
        EntityMapData mapData = map.getEntityMapData();

        try
        {
            // Execute logic for each entity
            executeEntityLogic(mapData);

            // Perform collision detection
            performCollisionDetection(map, mapData);

            // Build and distribute update packets
            sendEntityUpdatesToPlayers();

            // Update state of entities
            updateEntityStates(mapData);
        }
        catch (Exception e)
        {
            LOG.error("Exception during entity-manager logic", e);
        }
    }

    private void executeEntityLogic(EntityMapData mapData)
    {
        synchronized (mapData.entities)
        {
            for (Entity entity : mapData.entities.values())
            {
                // We won't run logic for deleted or dead enities
                if (!entity.isDeleted())
                {
                    entity.eventLogic(controller);
                }
            }
        }
    }

    private void performCollisionDetection(WorldMap map, EntityMapData mapData)
    {
        // Fetch map boundaries
        float mapMaxX = map.getMaxX();
        float mapMaxY = map.getMaxY();

        // Perform collision check for each entity
        synchronized (mapData.entities)
        {
            for (Entity entityA : mapData.entities.values())
            {
                performCollisionDetection(mapData, mapMaxX, mapMaxY, entityA);
            }
        }
    }

    private void performCollisionDetection(EntityMapData mapData, float mapMaxX, float mapMaxY, Entity entity)
    {
        // Check entity is not deleted or dead
        if (!entity.isDeleted())
        {
            performCollisionDetectionEntities(mapData, entity);

            // Check entity has still not been deleted or dead
            if (!entity.isDeleted())
            {
                performCollisionDetectionMap(mapMaxX, mapMaxY, entity);
            }
        }
    }

    private void performCollisionDetectionEntities(EntityMapData mapData, Entity entityA)
    {
        // Fetch potential entities from quad-tree
        Set<Entity> nearbyEntities = mapData.quadTree.getCollidableEntities(entityA);
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
                    packet.build(playerInfo);

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

    private void updateEntityStates(EntityMapData mapData)
    {
        synchronized (mapData.entities)
        {
            // Iterate each entity and transition their state
            Iterator<Map.Entry<Short, Entity>> iterator = mapData.entities.entrySet().iterator();

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
