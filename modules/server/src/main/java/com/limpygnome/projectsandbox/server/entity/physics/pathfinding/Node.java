package com.limpygnome.projectsandbox.server.entity.physics.pathfinding;

import com.limpygnome.projectsandbox.server.world.Map;

/**
 * Created by limpygnome on 01/09/15.
 */
public class Node
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
        this.cachedX = (float) tileX * (float) map.tileSize;
        this.cachedY = (float) tileY * (float) map.tileSize;
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
}
