package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.ents.Entity;

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
}
