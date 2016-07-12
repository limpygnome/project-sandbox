package com.projectsandbox.components.server.world.spawn;

import java.io.Serializable;

/**
 * Used to hold information to respawn an entity.
 */
public class Spawn implements Serializable
{
    private static final long serialVersionUID = 1L;

    public float x;
    public float y;
    public float z;
    public float rotation;
    
    public Spawn(float x, float y, float rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public Spawn(float x, float y, float z, float rotation)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
    }

    @Override
    public String toString()
    {
        return "Spawn{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rotation=" + rotation +
                '}';
    }

}
