package com.limpygnome.projectsandbox.server.ents.physics.casting.victims;

/**
 * Created by limpygnome on 07/05/15.
 */
public class MapCastVictim extends AbstractCastVictim
{
    public int tileIndexX;
    public int tileIndexY;

    public MapCastVictim(int tileIndexX, int tileIndexY)
    {
        this.tileIndexX = tileIndexX;
        this.tileIndexY = tileIndexY;
    }

    @Override
    public String toString()
    {
        return "[map victim - tileIndexX: " + tileIndexX + ", tileIndexY: " + tileIndexY + "]";
    }
}
