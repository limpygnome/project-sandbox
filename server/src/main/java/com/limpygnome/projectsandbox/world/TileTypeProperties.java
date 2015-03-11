package com.limpygnome.projectsandbox.world;

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
    
    public TileTypeProperties()
    {
        // Set default values
        solid = false;
        damage = 0;
    }
    
    public void parse(JSONObject obj) throws IIOException
    {
        // Damage
        if(obj.containsKey("damage"))
        {
            damage = (short) (long) obj.get("damage");
        }
        
        // Solid
        if(obj.containsKey("solid"))
        {
            solid = (boolean) obj.get("solid");
        }
    }

    @Override
    public String toString()
    {
        return "[solid: " + solid + ", damage: " + damage + "]";
    }
    
}
