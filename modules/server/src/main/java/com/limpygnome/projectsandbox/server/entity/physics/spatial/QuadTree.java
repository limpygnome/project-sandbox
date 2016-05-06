package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void update(Entity entity)
    {
        if (entityToNode.containsKey(entity))
        {
            // Update entity by recursing from old node (most likely moved to neighbor)
            QuadTreeNode oldNode = entityToNode.get(entity);
            QuadTreeNode newNode = oldNode.updateEntity(entity);

            if (oldNode != newNode)
            {
                entityToNode.put(entity, newNode);
            }
        }
        else
        {
            QuadTreeNode node = rootNode.findNodeForEntity(entity);

            if (node != null)
            {
                // Add mapping for entity
                entityToNode.put(entity, node);
            }
            else
            {
                LOG.warn("Could not insert entity into quad-tree - entity id: {}", entity.id);
            }
        }
    }

    /**
     * Removes the entity from this quad-tree.
     *
     * @param entity the entity
     */
    public void remove(Entity entity)
    {
        QuadTreeNode node = entityToNode.get(entity);

        if (node != null)
        {
            node.entities.remove(entity);
            entityToNode.remove(entity);
        }
    }

    public List<Entity> getCollidableEntities(Entity entity)
    {
        // Fetch all nodes to which entity intersects
        // -- just get node and work way up tree until null; big entity cud be in parent, so shud be added to result...
        // -- add all entities from nodes to actual returned result
    }

    public List<Entity> getEntitiesWithinRadius(Vector2 position, float radius)
    {
        // calculate bounding box
        // work way down tree and test intersection with calculated bounds, add nodes to result
    }

}
