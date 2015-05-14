package com.limpygnome.projectsandbox.server.ents.physics.proximity;

import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;

/**
 * Created by limpygnome on 13/05/15.
 */
public class ProximityResult implements Comparable
{
    public Entity entity;
    public float distance;

    /**
     * Only set when testing all vertices.
     */
    public Vector2 closestVertex;

    /**
     * Only set when testing all vertices.
     */
    public Vector2 closestVertexEntity;

    public ProximityResult(Entity entity, float distance)
    {
        this.entity = entity;
        this.distance = distance;
        this.closestVertex = null;
        this.closestVertexEntity = null;
    }

    public ProximityResult(Entity entity, float distance, Vector2 closestVertex, Vector2 closestVertexEntity)
    {
        this.entity = entity;
        this.distance = distance;
        this.closestVertex = closestVertex;
        this.closestVertexEntity = closestVertexEntity;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ProximityResult)
        {
            ProximityResult b = (ProximityResult) o;
            return (int) (distance - b.distance);
        }
        else
        {
            return 0;
        }
    }
}
