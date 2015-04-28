package com.limpygnome.projectsandbox.website.world;

import javax.imageio.IIOException;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class TileTypeProperties
{
    public boolean solid;
    public short damage;
    public float height;
    
    public TileTypeProperties()
    {
        // Set default values
        solid = false;
        damage = 0;
        height = 0.0f;
    }
    
    public void parse(JSONObject obj) throws IIOException
    {
        // Damage
        if (obj.containsKey("damage"))
        {
            damage = (short) (long) obj.get("damage");
        }
        
        // Solid
        if (obj.containsKey("solid"))
        {
            solid = (boolean) obj.get("solid");
        }
        
        // Height
        if (obj.containsKey("height"))
        {
           height = (float) (double) obj.get("height");
        }
    }

    @Override
    public String toString()
    {
        return "[solid: " + solid + ", damage: " + damage + ", height: " + height + "]";
    }
}
