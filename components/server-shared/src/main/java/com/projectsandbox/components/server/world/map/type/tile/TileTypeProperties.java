package com.projectsandbox.components.server.world.map.type.tile;

import javax.imageio.IIOException;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class TileTypeProperties
{
    /**
     * Indicates the tile is solid.
     */
    public boolean solid;

    /**
     * The amount of damage caused by the tile.
     *
     * TODO: consider refactoring into float to allow negative damage i.e. healing?
     */
    public short damage;

    /**
     * The height of the block, for cube tiles.
     */
    public float height;

    /**
     * Indicates if the tile is a pedestrian zone, walkable by AI.
     */
    public boolean pedestrian;
    
    public TileTypeProperties()
    {
        // Set default values
        solid = false;
        damage = 0;
        height = 0.0f;
        pedestrian = false;
    }
    
    public void parse(JSONObject obj) throws IIOException
    {
        // Damage
        if (obj.containsKey("damage"))
        {
            damage = (short) (long) obj.get("damage");
        }
        
        // Solid

        // Height
        if (obj.containsKey("height"))
        {
            height = (float) (double) obj.get("height");
        }
        if (obj.containsKey("solid"))
        {
            solid = (boolean) obj.get("solid");
        }

        // Pedestrian
        if (obj.containsKey("pedestrian"))
        {
            pedestrian = (boolean) obj.get("pedestrian");
        }
    }

    @Override
    public String toString()
    {
        return "tileTypeProperties{ solid: " + solid + ", damage: " + damage + ", height: " + height + ", pedestrian: " + pedestrian + "}";
    }
}
