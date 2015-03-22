package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractCar extends Entity
{
    protected float accelerationFactor;
    protected float maxSpeed;
    protected float friction;
    
    public AbstractCar(short width, short height)
    {
        super(width, height);
    }

    @Override
    public strictfp void eventCollision(Entity collider, CollisionResult result)
    {
        if (collider instanceof Player)
        {
            // We won't move if the ent is a player
            collider.positionOffset(result.mtv);
        }
        else
        {
            // Perform default behaviour
            super.eventCollision(collider, result);
        }
    }
}
