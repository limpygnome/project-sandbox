package com.limpygnome.projectsandbox.ents.physics;

/**
 *
 * @author limpygnome
 */
public class CollisionResultMap
{
    public CollisionResult result;
    public int tileX;
    public int tileY;
    
    public CollisionResultMap(CollisionResult result, int tileX, int tileY)
    {
        this.result = result;
        this.tileX = tileX;
        this.tileY = tileY;
    }
}
