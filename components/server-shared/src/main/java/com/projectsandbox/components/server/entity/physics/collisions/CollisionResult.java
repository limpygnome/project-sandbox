package com.projectsandbox.components.server.entity.physics.collisions;

import com.projectsandbox.components.server.entity.physics.Vector2;

/**
 *
 * @author limpygnome
 */
public class CollisionResult
{
    public boolean collision;
    public Vector2 mtv;
    
    public Vector2 axis;
    public float depth;
    
    public CollisionResult(boolean collision, Vector2 mtv, Vector2 axis, float depth)
    {
        this.collision = collision;
        this.mtv = mtv;
        this.axis = axis;
        this.depth = depth;
    }

    @Override
    public String toString()
    {
        return "[collision: " + collision + ", mtv: " + mtv + ", axis: " + axis +  ", depth: " + depth + "]";
    }
    
}
