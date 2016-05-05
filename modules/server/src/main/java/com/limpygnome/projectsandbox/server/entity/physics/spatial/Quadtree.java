package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Created by limpygnome on 05/05/16.
 */
public class Quadtree
{
    private static final int MAX_DEPTH = 4;

    QuadtreeNode rootNode;

    public Quadtree(WorldMap worldMap)
    {
        rootNode = new QuadtreeNode(MAX_DEPTH, 0.0f, 0.0f, worldMap.getMaxX(), worldMap.getMaxY());
    }

    public void add(Entity entity)
    {
    }

    public void update(Entity entity)
    {
    }

    public void remove(Entity entity)
    {
    }

    private QuadtreeNode findNode(Entity entity)
    {
    }

}
