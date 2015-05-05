package com.limpygnome.projectsandbox.server.ents.physics;

import com.limpygnome.projectsandbox.server.ents.Entity;

/**
 *
 * @author limpygnome
 */
public class CastingResult
{
    public boolean collision;
    
    public CollisionResult collisionResult;
    public float distance;
    
    public Entity victim;
    
    public CastingResult()
    {
        collision = false;
        collisionResult = null;
        distance = -1.0f;
        victim = null;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("[collision: ").append(collision).append(", distance: ").append(distance).append(", ");
        sb.append("victim: ").append(victim).append(", collision result: ").append(collisionResult).append("]");

        return sb.toString();
    }
}
