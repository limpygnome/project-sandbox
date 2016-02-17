package com.limpygnome.projectsandbox.server.world.map;

/**
 * An immutable object used to represent a position in a map.
 */
public class MapPosition
{
    public final WorldMap map;
    public final float x;
    public final float y;
    public final int tileX;
    public final int tileY;

    public MapPosition(WorldMap map, float x, float y, int tileX, int tileY)
    {
        this.map = map;
        this.x = x;
        this.y = y;
        this.tileX = tileX;
        this.tileY = tileY;
    }
}
