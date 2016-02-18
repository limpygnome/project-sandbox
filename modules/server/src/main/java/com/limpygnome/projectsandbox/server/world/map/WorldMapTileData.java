package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.world.tile.TileType;

/**
 * Used to hold tile data for an instance of {@link WorldMap}.
 */
public class WorldMapTileData
{
    public TileType[] tileTypes;

    public float tileSize;
    public float tileSizeHalf;
    public float tileSizeQuarter;

    public short widthTiles;
    public short heightTiles;
    public float maxX;
    public float maxY;

    /** Mapped by [y][x] or [row][column]; bottom-left at 0, top-right at n */
    public short tiles[][];

    public Vertices[][] tileVertices;

    /**
     * Safely retrieves the tile-type for the specified position.
     *
     * This will perform checks on the position.
     *
     * @param tileX The tile X position
     * @param tileY The tile Y position
     * @return Instance, or null if invalid position
     */
    public synchronized TileType tileTypeFromPosition(int tileX, int tileY)
    {
        // Check within bounds of map
        if (tileX < 0 || tileY < 0 || tileX >= widthTiles || tileY >= heightTiles)
        {
            return null;
        }

        // Retrieve tile-type at co-ordinate
        short tileTypeId = tiles[tileY][tileX];

        // Return the type, since tile ID is the same as array index
        return tileTypes[tileTypeId];
    }

}
