package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
import com.limpygnome.projectsandbox.server.ents.physics.CollisionResultMap;
import com.limpygnome.projectsandbox.server.ents.physics.SAT;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author limpygnome
 */
public class EntityManager
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    private final Controller controller;
    public final HashMap<Short, Entity> entities;
    public final HashMap<Short, Entity> entitiesNew;
    private short entityIdCounter;

    public EntityManager(Controller controller)
    {
        this.controller = controller;
        this.entities = new HashMap<>();
        this.entitiesNew = new HashMap<>();
    }

    public Entity fetch(Short key)
    {
        synchronized (entities)
        {
            return entities.get(key);
        }
    }

    public boolean add(Entity ent)
    {
        // Fetch the next available identifier
        short id;
        boolean foundNewId = false;
        int attempts = 0;

        synchronized (entities)
        {
            do
            {
                id = entityIdCounter++;
                if (!entities.containsKey(id) && !entitiesNew.containsKey(id))
                {
                    foundNewId = true;
                }
            }
            while (!foundNewId && ++attempts < Short.MAX_VALUE);
        }

        // Check we found an identifier
        if (!foundNewId)
        {
            return false;
        }

        // Assign id to entity
        ent.id = id;

        // Add mapping
        synchronized (entities)
        {
            // Add entity to pending map
            entitiesNew.put(id, ent);

            // Update slotState to created - for update to all players!
            ent.setState(StateChange.CREATED);

            LOG.debug("Ent added - {}", ent);
        }

        return true;
    }

    public boolean remove(short id)
    {
        // Remove mapping
        synchronized (entities)
        {
            Entity ent = entities.get(id);
            if (ent != null)
            {
                ent.setState(StateChange.PENDING_DELETED);
                ent.eventPendingDeleted(controller);

                LOG.debug("Ent set for removal - {}", ent);
            } else
            {
                // Try pending (new) ents
                ent = entitiesNew.get(id);
                if (ent != null)
                {
                    ent.setState(StateChange.PENDING_DELETED);
                    LOG.debug("Ent (new) set for removal - {}", ent);
                }
            }
        }

        return true;
    }

    public boolean remove(Entity ent)
    {
        return remove(ent.id);
    }

    public void logic()
    {
        try
        {
            synchronized (entities)
            {
                Entity a;
                Entity b;

                // Fetch map boundries
                // TODO: update if we have multiple maps
                float mapMaxX = controller.mapManager.main.maxX;
                float mapMaxY = controller.mapManager.main.maxY;

                // Perform collision check for each entity
                CollisionResult result;
                Collection<CollisionResultMap> mapResults;

                for (Map.Entry<Short, Entity> kv : entities.entrySet())
                {
                    a = kv.getValue();

                    // Perform ent logic
                    a.logic(controller);

                    // Perform collision detection/handling with other ents
                    for (Map.Entry<Short, Entity> kv2 : entities.entrySet())
                    {
                        b = kv2.getValue();

                        if (a.id != b.id)
                        {
                            result = SAT.collision(a, b);

                            if (result.collision)
                            {
                                // Inform both ents of event
                                a.eventCollision(controller, b, a, b, result);
                                b.eventCollision(controller, b, a, a, result);
                            }
                        }
                    }

                    // Perform collision with map
                    mapResults = SAT.collisionMap(controller.mapManager.main, a);

                    for (CollisionResultMap mapResult : mapResults)
                    {
                        // Check if solid for collision response
                        if (mapResult.tileType.properties.solid)
                        {
                            a.positionOffset(mapResult.result.mtv);
                        }

                        // Check if to apply damage
                        if (mapResult.tileType.properties.damage != 0)
                        {
                            // TODO: apply damage from tile
                        }
                    }

                    // Update position for ent
                    a.position.copy(a.positionNew);

                    // Check ent is not outside map
                    if (a.positionNew.x < 0.0f || a.positionNew.y < 0.0f ||
                            a.positionNew.x > mapMaxX || a.positionNew.y > mapMaxY)
                    {
                        // Kill the ent...
                        a.kill(controller);
                    }
                }

                // Add pending ents
                entities.putAll(entitiesNew);
                entitiesNew.clear();
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception during logic", e);
        }
    }

    // TODO: consider removal of the below code
    public Entity[] nearbyEnts(Entity a, float distance, boolean testAllVertices)
    {
        LinkedList<Entity> result = new LinkedList<>();

        synchronized (entities)
        {
            Entity b;
            float entDistance;
            boolean found;
            int i, j;

            for (Map.Entry<Short, Entity> kv : entities.entrySet())
            {
                b = kv.getValue();

                // Get distance to center
                entDistance = Vector2.distance(a.position, b.position);

                // Check distance to center
                if (entDistance <= distance)
                {
                    result.add(b);
                }
                else if
                (
                    testAllVertices &&
                    // Must be within collision radius to even be testable
                    entDistance <= distance + b.cachedVertices.collisionRadius
                )
                {
                    // Test all the vertices - expensive!
                    found = false;
                    for (i = 0; !found && i < a.cachedVertices.vertices.length; i++)
                    {
                        for (j = 0; !found && j < b.cachedVertices.vertices.length; j++)
                        {
                            entDistance = Vector2.distance(a.cachedVertices.vertices[i], b.cachedVertices.vertices[i]);

                            if (entDistance < distance)
                            {
                                found = true;
                                result.add(b);
                            }
                        }
                    }
                }
            }
        }

        return result.toArray(new Entity[result.size()]);
    }

}