package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

        int newDepth = maxDepth - 1;

        if (newDepth >= 0)
        {
            this.childNodes = new QuadTreeNode[4];

            // Split into four quadrants...
            float widthHalf = (upperX - lowerX) / 2.0f;
            float heightHalf = (upperY - lowerY) / 2.0f;

            // Bottom-left quad
            this.childNodes[0] = new QuadTreeNode(this, newDepth, lowerX, lowerY, lowerX + widthHalf, lowerY + heightHalf);

            // Bottom-right quad
            this.childNodes[1] = new QuadTreeNode(this, newDepth, lowerX + widthHalf, lowerY, upperX, lowerY + heightHalf);

            // Top-left quad
            this.childNodes[2] = new QuadTreeNode(this, newDepth, lowerX, lowerY + heightHalf, lowerX + widthHalf, upperY);

            // Top-right quad
            this.childNodes[3] = new QuadTreeNode(this, newDepth, lowerX + widthHalf, lowerY + heightHalf, upperX, upperY);
        }
        else
        {
            this.childNodes = new QuadTreeNode[0];
        }
    }

    /*
        Checks if the entity is contained within this node.
     */
    boolean contains(Entity entity)
    {
        // TODO: ask entity for bounding box, which entity cache's; or at least their largest size...
        float maxSizeHalf = entity.cachedVertices.collisionRadius / 2.0f;
        float x = entity.position.x;
        float y = entity.position.y;

        return contains(x - maxSizeHalf, y - maxSizeHalf, x + maxSizeHalf, y + maxSizeHalf);
    }

    boolean contains(float lowerX, float lowerY, float upperX, float upperY)
    {
        return lowerX >= this.lowerX && lowerY >= this.lowerY && upperX <= this.upperX && upperY <= this.upperY;
    }

    /*
        Determines if this node intersects with the region specified.
     */
    boolean intersects(float lowerX, float lowerY, float upperX, float upperY)
    {
        return (upperX >= this.lowerX && lowerX < this.upperX && upperY > this.lowerY && lowerY < this.upperY);
    }

    /*
        Used to go from the top node and find the node for an entity.
     */
    QuadTreeNode findNodeForEntity(Entity entity)
    {
        QuadTreeNode result = this;

        for (QuadTreeNode node : childNodes)
        {
            if (node.contains(entity))
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

        if (contains(entity))
        {
            // Check if we can fit into a child node yet...
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
            // Start search using neighboring nodes
            result = parentNode.updateEntity(entity);
        }
        else
        {
            result = null;
        }

        return result;
    }

    void addEntitiesAndRecurseChildNodes(Set<Entity> result)
    {
        // Add current entities
        result.addAll(entities);

        // Traverse child nodes
        for (QuadTreeNode node : childNodes)
        {
            node.addEntitiesAndRecurseChildNodes(result);
        }
    }

    void addEntitiesAndRecurseFittingChildNodes(Set<Entity> result, float lowerX, float lowerY, float upperX, float upperY)
    {
        result.addAll(entities);

        // Traverse nodes which fit lower/upper co-ords
        for (QuadTreeNode node : childNodes)
        {
            if (node.intersects(lowerX, lowerY, upperX, upperY))
            {
                node.addEntitiesAndRecurseFittingChildNodes(result, lowerX, lowerY, upperX, upperY);
            }
        }
    }

}
