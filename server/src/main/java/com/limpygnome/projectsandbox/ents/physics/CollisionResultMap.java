package com.limpygnome.projectsandbox.ents.physics;

import com.limpygnome.projectsandbox.world.TileType;

/**
 *
 * @author limpygnome
 */
public class CollisionResultMap
{
    public CollisionResult result;
    public int tileX;
    public int tileY;
    public TileType tileType;
    
    public CollisionResultMap(CollisionResult result, int tileX, int tileY, TileType tileType)
    {
        this.result = result;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileType = tileType;
    }
}
