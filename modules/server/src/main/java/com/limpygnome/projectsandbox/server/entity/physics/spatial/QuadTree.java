package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Quad-tree for spatial partitioning of entities.
 */
public class QuadTree
{
    private final static Logger LOG = LogManager.getLogger(QuadTreeNode.class);

    private static final int MAX_DEPTH = 4;

    private QuadTreeNode rootNode;
    private Map<Entity, QuadTreeNode> entityToNode;

    public QuadTree(WorldMap worldMap)
    {
        rootNode = new QuadTreeNode(null, MAX_DEPTH, 0.0f, 0.0f, worldMap.getMaxX(), worldMap.getMaxY());
        entityToNode = new HashMap<>();
    }

    /**
     * Used to either add or update the entity within this quad-tree.
     *
     * @param entity the entity
     */
    public synchronized void update(Entity entity)
    {
        TODO: make entitymanager explicitly put entity into tree; updates when entity not mapped to node -> ignore..
        currently an issue whereby entity position updated before entity in world, thus added to scene and update sent
            to player without ent id.

        QuadTreeNode oldNode = null;
        QuadTreeNode newNode = null;

        // Determine if node needs to be set/changed
        if (entityToNode.containsKey(entity))
        {
            // Update entity by recursing from old node (most likely moved to neighbor)
            oldNode = entityToNode.get(entity);
            newNode = oldNode.updateEntity(entity);

            // Check the two nodes are different...
            if (oldNode == newNode)
            {
                // Don't bother updating...
                oldNode = null;
                newNode = null;
            }
        }
        else
        {
            newNode = rootNode.findNodeForEntity(entity);

            if (newNode != null)
            {
                // Add mapping for entity
                entityToNode.put(entity, newNode);
            }
            else
            {
                LOG.warn("Could not insert entity into quad-tree - entity id: {}", entity.id);
            }
        }

        if (oldNode != null)
        {
            // Remove entity from old node
            oldNode.entities.remove(entity);
        }

        if (newNode != null)
        {
            // Add entity to new node
            newNode.entities.add(entity);

            // Update mapping for entity -> node
            entityToNode.put(entity, newNode);
        }
    }

    /**
     * Removes the entity from this quad-tree.
     *
     * @param entity the entity
     */
    public synchronized void remove(Entity entity)
    {
        // TODO: call this when entity marked for deletion; saves checking if ents are pending deleted...
        QuadTreeNode node = entityToNode.get(entity);

        if (node != null)
        {
            node.entities.remove(entity);
            entityToNode.remove(entity);
        }
    }

    /**
     * Fetches entities which may be able to collide with the provided entity.
     *
     * @param entity the entity
     * @return list of possible entities
     */
    public synchronized Set<Entity> getCollidableEntities(Entity entity)
    {
        Set<Entity> result = new HashSet<>();

        // Fetch node of entity
        QuadTreeNode entityNode = entityToNode.get(entity);

        // Add all entities in child nodes
        entityNode.addEntitiesAndRecurseChildNodes(result);

        // Traverse up to parent and add all entities along the way
        // -- May be too big to fit into current or child quadtree nodes
        QuadTreeNode currentNode = entityNode;

        while ((currentNode = currentNode.parentNode) != null)
        {
            result.addAll(currentNode.entities);
        }

        return result;
    }

    /**
     * Fetches entities within a radius of another entity.
     *
     * @param entity the entity
     * @param radius the radius
     * @return list of entities within radius of entity
     */
    public synchronized Set<Entity> getEntitiesWithinRadius(Entity entity, float radius)
    {
        return getEntitiesWithinRadius(entity.position, radius);
    }

    public synchronized Set<Entity> getEntitiesWithinRadius(Vector2 position, float radius)
    {
        Set<Entity> result = new HashSet<>();

        // Recurse nodes to add all items in quads which can fit the position
        rootNode.addEntitiesAndRecurseFittingChildNodes(result, position.x - radius, position.y - radius, position.x + radius, position.y + radius);

        // Now filter entities in result not within radius
        Iterator<Entity> iterator = result.iterator();

        Entity entity;
        float entityDistance;

        while (iterator.hasNext())
        {
            entity = iterator.next();
            entityDistance = Vector2.distance(entity.positionNew, position);

            if (entityDistance >= radius)
            {
                iterator.remove();
            }
        }

        return result;
    }

}
