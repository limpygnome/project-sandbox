package com.projectsandbox.components.server.world.spawn;

/**
 *
 * @author limpygnome
 */
public class Spawn
{
    public float x;
    public float y;
    public float rotation;
    
    public Spawn(float x, float y, float rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    @Override
    public String toString()
    {
        return "[x: " + x + ", y: " + y + ", rotation: " + rotation + "]";
    }
}
