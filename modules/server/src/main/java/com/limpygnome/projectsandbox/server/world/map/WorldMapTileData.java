package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.world.tile.TileType;

/**
 * Used to hold tile data for an instance of {@link WorldMap}.
 */
public class WorldMapTileData
{
    public TileType[] tileTypes;

    /** Mapped by [y][x] or [row][column]; bottom-left at 0, top-right at n */
    public short tiles[][];

    public Vertices[][] tileVertices;
}
