package com.limpygnome.projectsandbox.ents.physics;

import com.limpygnome.projectsandbox.ents.Entity;

/**
 *
 * @author limpygnome
 */
public class CastingResult
{
    public boolean collision;
    
    public float collisionX;
    public float collisionY;
    
    public Entity victim;
    
    public CastingResult()
    {
        collision = false;
        victim = null;
    }
}
