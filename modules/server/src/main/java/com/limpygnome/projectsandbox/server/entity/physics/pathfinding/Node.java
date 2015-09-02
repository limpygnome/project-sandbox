package com.limpygnome.projectsandbox.server.entity.physics.pathfinding;

import com.limpygnome.projectsandbox.server.world.Map;

/**
 * Created by limpygnome on 01/09/15.
 */
public class Node implements Comparable
{
    public Node parent;

    public int tileX;
    public int tileY;
    public float pathCost;
    public float heuristicCost;
    public int searchDepth;

    public float cachedX;
    public float cachedY;

    public Node(int tileX, int tileY)
    {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public Node(int tileX, int tileY, float pathCost, int searchDepth)
    {
        this.tileX = tileX;
        this.tileY = tileY;
        this.pathCost = pathCost;
        this.searchDepth = searchDepth;
    }

    void buildAndCacheXY(Map map)
    {
        float tileSize = (float) map.tileSize;
        float tileSizeHalf = (float) map.tileSize / 2.0f;

        this.cachedX = ((float) tileX * tileSize) + tileSizeHalf;
        this.cachedY = ((float) tileY * tileSize) + tileSizeHalf;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (tileX != node.tileX) return false;
        return tileY == node.tileY;

    }

    @Override
    public int hashCode()
    {
        int result = tileX;
        result = 31 * result + tileY;
        return result;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o == null || !(o instanceof Node))
        {
            throw new RuntimeException("Invalid object for comparison - " + o);
        }

        Node node = (Node) o;

        return (int) (heuristicCost - node.heuristicCost);
    }

    @Override
    public String toString()
    {
        return "[tx: " + tileX + ", ty: " + tileY + ", x: " + cachedX + ", y: " + cachedY + "]";
    }
}
