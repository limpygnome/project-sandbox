package com.projectsandbox.components.server.entity.ai.pathfinding;

/**
 * Created by limpygnome on 01/09/15.
 */
public class TilePosition
{
    int tileX;
    int tileY;

    public TilePosition(int tileX, int tileY)
    {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TilePosition that = (TilePosition) o;

        if (tileX != that.tileX) return false;
        return tileY == that.tileY;

    }

    @Override
    public int hashCode()
    {
        int result = tileX;
        result = 31 * result + tileY;
        return result;
    }
}
