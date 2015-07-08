package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.ents.death.MapBoundsKiller;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.enums.StateChange;
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
 * @author limpygnome
 */
public class EntityManager implements IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(EntityManager.class);

    private final Controller controller;
    public final HashMap<Short, Entity> entities;
    public final HashMap<Short, Entity> entitiesNew;
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

    public boolean add(Entity ent)
    {
        // Fetch the next available identifier
        Short id = idCounterProvider.nextId(ent.id);

        // Check we found an identifier
        if (id == null)
        {
            LOG.error("Unable to create identifier for entity - {}", ent);
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

    @Override
    public boolean containsId(short id)
    {
        return entities.containsKey(id) || entitiesNew.containsKey(id);
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

                // Execute logic for each entity
                for (Map.Entry<Short, Entity> kv : entities.entrySet())
                {
                    a = kv.getValue();

                    if (a.getState() != StateChange.PENDING_DELETED && a.getState() != StateChange.DELETED)
                    {
                        a.logic(controller);
                    }
                }

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

                    // Perform ent logic if not in removal process; this
                    // avoids a situation where an ent might get deleted, but
                    // sets its self to changed i.e. god mode / never deletes

                    if (a.getState() != StateChange.PENDING_DELETED &&
                            a.getState() != StateChange.DELETED)
                    {
                        // Perform collision detection/handling with other ents
                        for (Map.Entry<Short, Entity> kv2 : entities.entrySet())
                        {
                            b = kv2.getValue();

                            if (a.id != b.id && !b.physicsStatic)
                            {
                                result = SAT.collision(a, b);

                                if (result.collision)
                                {
                                    // Inform both ents of event
                                    a.eventHandleCollision(controller, b, a, b, result);
                                    b.eventHandleCollision(controller, b, a, a, result);
                                }
                            }
                        }

                        // Perform collision with map
                        mapResults = SAT.collisionMap(controller.mapManager.main, a);

                        for (CollisionResultMap mapResult : mapResults)
                        {
                            a.eventHandleCollisionMap(controller, mapResult);
                        }

                        // Update position for ent
                        a.position.copy(a.positionNew);

                        // Check ent is not outside map
                        if (a.positionNew.x < 0.0f || a.positionNew.y < 0.0f ||
                                a.positionNew.x > mapMaxX || a.positionNew.y > mapMaxY)
                        {
                            // Kill the ent...
                            a.kill(controller, null, MapBoundsKiller.class);
                        }
                    }
                }

                // Add pending ents
                entities.putAll(entitiesNew);
                entitiesNew.clear();

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
