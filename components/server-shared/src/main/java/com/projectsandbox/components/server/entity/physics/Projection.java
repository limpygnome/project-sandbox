package com.projectsandbox.components.server.entity.physics;

/**
 *
 * @author limpygnome
 */
public class Projection
{
    public float min;
    public float max;
    
    public Projection(float min, float max)
    {
        this.min = min;
        this.max = max;
    }
    
    public float overlap(Projection p)
    {
        if (min < p.min)
        {
            return p.min - max;
        }
        else
        {
            return min - p.max;
        }
    }
}
