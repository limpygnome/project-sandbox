package com.projectsandbox.components.server.entity;

import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limpygnome on 21/07/16.
 */
public class EntityMapData
{
    /* Used for efficient collision detection and network updates. */
    protected QuadTree quadTree;

    /* A map of entity id -> entity. */
    protected final Map<Short, Entity> entities;

    public EntityMapData()
    {
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

}
