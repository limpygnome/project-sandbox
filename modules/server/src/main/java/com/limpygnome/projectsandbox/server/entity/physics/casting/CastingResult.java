package com.limpygnome.projectsandbox.server.entity.physics.casting;

import com.limpygnome.projectsandbox.server.entity.physics.casting.victims.AbstractCastVictim;

/**
 *
 * @author limpygnome
 */
public class CastingResult
{
    public boolean collision;
    public float x;
    public float y;
    public float distance;

    public AbstractCastVictim victim;
    
    public CastingResult()
    {
        collision = false;
        victim = null;
    }

    public CastingResult(float x, float y, float distance)
    {
        this();

        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb  .append("[collision: ").append(collision).append(", distance: ").append(distance).append(", x: ")
            .append(x).append(", y: ").append(y).append(". victim: ").append(victim != null ? victim : "null").append("]");

        return sb.toString();
    }
}
