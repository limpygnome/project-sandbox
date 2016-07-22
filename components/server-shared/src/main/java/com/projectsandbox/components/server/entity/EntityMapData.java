package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.util.IdCounterProvider;
import com.projectsandbox.components.server.util.counters.IdCounterConsumer;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limpygnome on 21/07/16.
 */
public class EntityMapData implements IdCounterConsumer
{
    private final static Logger LOG = LogManager.getLogger(EntityMapData.class);

    /* Used for efficient collision detection and network updates. */
    protected QuadTree quadTree;

    /* A map of entity id -> entity. */
    protected final Map<Short, Entity> entities;

    /* Counter for producing entity identifiers. */
    private IdCounterProvider idCounterProvider;

    public EntityMapData()
    {
        this.idCounterProvider = new IdCounterProvider(this);
        this.quadTree = null;
        this.entities = new ConcurrentHashMap<>();
    }

    /**
     * To be invoked once a map has finished loading.
     *
     * @param map the map to which this belongs
     */
    public void setup(WorldMap map)
    {
        quadTree = new QuadTree(map);
    }

    @Override
    public boolean containsId(short id)
    {
        return entities.containsKey(id);
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

    protected synchronized void remove(Controller controller, Entity entity)
    {
        synchronized (entity)
        {
            if (entity.id != null && entities.containsKey(entity.id))
            {
                // Remove from quad-tree
                quadTree.remove(entity);

                // Mark entity for deletion
                entity.setState(EntityState.PENDING_DELETED);

                LOG.debug("Entity set for removal - ent id: {}", entity.id);

                // Invoke entity event handler
                entity.eventPendingDeleted(controller);
            }
        }
    }

    public QuadTree getQuadTree()
    {
        return quadTree;
    }

}
