package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * A node in a quad-tree.
 */
public class QuadTreeNode
{
    float lowerX;
    float lowerY;

    float upperX;
    float upperY;

    List<Entity> entities;

    QuadTreeNode parentNode;
    QuadTreeNode[] childNodes;

    public QuadTreeNode(QuadTreeNode parentNode, int maxDepth, float lowerX, float lowerY, float upperX, float upperY)
    {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;

        this.entities = new LinkedList<>();
        this.parentNode = parentNode;
        this.childNodes = new QuadTreeNode[4];

        int newDepth = maxDepth - 1;

        if (newDepth >= 0)
        {
            // Split into four quadrants...
            float widthHalf = (upperX - lowerX) / 2.0f;
            float heightHalf = (upperY - lowerY) / 2.0f;

            // Bottom-left quad
            childNodes[0] = new QuadTreeNode(this, newDepth, lowerX, lowerY, lowerX + widthHalf, lowerY + heightHalf);

            // Bottom-right quad
            childNodes[1] = new QuadTreeNode(this, newDepth, lowerX + widthHalf, lowerY, upperX, lowerY + heightHalf);

            // Top-left quad
            childNodes[2] = new QuadTreeNode(this, newDepth, lowerX, lowerY + heightHalf, lowerX + widthHalf, upperY);

            // Top-right quad
            childNodes[3] = new QuadTreeNode(this, newDepth, lowerX + widthHalf, lowerY + heightHalf, upperX, upperY);
        }
    }

    /*
        Checks if the entity intersects with this node.
     */
    boolean intersects(Entity entity)
    {
        // TODO: ask entity for bounding box, which entity cache's; or at least their largest size...
        float maxSize = entity.cachedVertices.collisionRadius / 2.0f;
        float x = entity.position.x;
        float y = entity.position.y;

        return x - maxSize >= lowerX && y - maxSize >= lowerY && x + maxSize <= upperX && y + maxSize <= upperY;
    }

    /*
        Used to go from the top node and find the node for an entity.
     */
    QuadTreeNode findNodeForEntity(Entity entity)
    {
        QuadTreeNode result = this;

        for (QuadTreeNode node : childNodes)
        {
            if (node.intersects(entity))
            {
                result = node.findNodeForEntity(entity);
            }
        }

        return result;
    }

    /*
        Used to find the new node for an entity already in a node. Allows for checking neighbors, rather than going
        from the top again.
     */
    QuadTreeNode updateEntity(Entity entity)
    {
        QuadTreeNode result;

        if (intersects(entity))
        {
            QuadTreeNode childNode = findNodeForEntity(entity);

            if (childNode != null)
            {
                result = childNode;
            }
            else
            {
                result = this;
            }
        }
        else if (parentNode != null)
        {
            result = parentNode.updateEntity(entity);
        }
        else
        {
            result = null;
        }

        return result;
    }

    void addEntitiesAndRecurseChildNodes(List<Entity> result)
    {
        // Add current entities
        result.addAll(entities);

        // Traverse child nodes
        for (QuadTreeNode node : childNodes)
        {
            addEntitiesAndRecurseChildNodes(result);
        }
    }

}
