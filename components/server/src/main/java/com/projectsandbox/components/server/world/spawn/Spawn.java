package com.projectsandbox.components.server.world.spawn;

/**
 *
 * @author limpygnome
 */
public class Spawn
{
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
