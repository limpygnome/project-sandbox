package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 05/05/16.
 */
public class QuadtreeNode
{
    float lowerX;
    float lowerY;
    float upperX;
    float upperY;
    List<Entity> entities;
    List<QuadtreeNode> childNodes;

    public QuadtreeNode(int maxDepth, float lowerX, float lowerY, float upperX, float upperY)
    {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;

        entities = new LinkedList<>();
        childNodes = new LinkedList<>();

        int newDepth = maxDepth - 1;

        if (newDepth >= 0)
        {
            // Split into four quadrants...
            float widthHalf = (upperX - lowerX) / 2.0f;
            float heightHalf = (upperY - lowerY) / 2.0f;

            QuadtreeNode childNode;

            // Bottom-left quad
            childNode = new QuadtreeNode(newDepth, lowerX, lowerY, lowerX + widthHalf, lowerY + heightHalf);
            childNodes.add(childNode);

            // Bottom-right quad
            childNode = new QuadtreeNode(newDepth, lowerX + widthHalf, lowerY, upperX, lowerY + heightHalf);
            childNodes.add(childNode);

            // Top-left quad
            childNode = new QuadtreeNode(newDepth, lowerX, lowerY + heightHalf, lowerX + widthHalf, upperY);
            childNodes.add(childNode);

            // Top-right quad
            childNode = new QuadtreeNode(newDepth, lowerX + widthHalf, lowerY + heightHalf, upperX, upperY);
            childNodes.add(childNode);
        }
    }

    boolean intersects(Entity entity)
    {
        // TOOD: ents are rotated, so pick largest width/height or ask entity for bounding box (best)
    }

    QuadtreeNode findNodeForEntity(Entity entity)
    {
        QuadtreeNode result = this;

        for (QuadtreeNode node : childNodes)
        {
            if (node.intersects(entity))
            {
                result = node.findNodeForEntity(entity);
            }
        }

        return result;
    }

}
