package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.world.tile.TileType;

/**
 * Used to hold tile data for an instance of {@link WorldMap}.
 */
public class WorldMapTileData
{
    private WorldMap map;

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
     * Creates a new instance.
     *
     * @param map the map to which this belongs
     */
    public WorldMapTileData(WorldMap map)
    {
        this.map = map;
    }

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

    /**
     * Determines the tile position from a vector.
     *
     * @param vector the vector of x,y
     * @return the result
     */
    public MapPosition positionFromReal(Vector2 vector)
    {
        return positionFromReal(vector.x, vector.y);
    }

    /**
     * Determines the position from 2D co-ordinates.
     *
     * @param x the x position
     * @param y the y position
     * @return the result
     */
    public MapPosition positionFromReal(float x, float y)
    {
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        return new MapPosition(
                map,
                x,
                y,
                tileX,
                tileY
        );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("tileData{");

        // Append tile properties
        sb.append("tile size: ").append(tileSize).append(",");
        sb.append("tile size half: ").append(tileSizeHalf).append(",");
        sb.append("tile size quart: ").append(tileSizeQuarter).append(",");
        sb.append("width tiles: ").append(widthTiles).append(",");
        sb.append("height tiles: ").append(heightTiles).append(",");
        sb.append("max x: ").append(maxX).append(",");
        sb.append("max y: ").append(maxY).append(",");

        // Append tile types
        sb.append("tileTypes{");
        for (TileType tileType : tileTypes)
        {
            sb.append(tileType);
        }
        sb.append("}");

        return sb.toString();
    }
}
