package com.limpygnome.projectsandbox.server.entity.physics.spatial;

import com.limpygnome.projectsandbox.server.entity.Entity;

/**
 * Created by limpygnome on 13/05/15.
 */
public class ProximityResult implements Comparable<ProximityResult>
{
    public Entity entity;
    public float distance;

    public ProximityResult(Entity entity, float distance)
    {
        this.entity = entity;
        this.distance = distance;
    }

    @Override
    public int compareTo(ProximityResult o)
    {
        if (distance > o.distance)
        {
            return 1;
        }
        else if (distance < o.distance)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

}
