package com.limpygnome.projectsandbox.server.entity.physics.collisions.map;

import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.world.map.type.tile.TileType;

/**
 * Created by limpygnome on 15/04/16.
 */
public class CollisionTileMapResult implements CollisionMapResult
{
    public CollisionResult result;
    public int tileX;
    public int tileY;
    public TileType tileType;

    public CollisionTileMapResult(CollisionResult result, int tileX, int tileY, TileType tileType)
    {
        this.result = result;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileType = tileType;
    }

}
