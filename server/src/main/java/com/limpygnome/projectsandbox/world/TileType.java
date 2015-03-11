package com.limpygnome.projectsandbox.world;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.textures.Texture;
import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class TileType
{
    // auto-gen by map
    public short id;
    
    public String name;
    public Texture texture;
    public TileTypeProperties properties;
    
    public TileType()
    {
        this.properties = new TileTypeProperties();
    }
    
    public static TileType load(Controller controller, JSONObject obj) throws IOException
    {
        TileType type = new TileType();
        
        // Parse attributes
        type.name = (String) obj.get("name");

        // Fetch texture
        String textureName = (String) obj.get("texture");
        type.texture = controller.textureManager.textureFiles.get(textureName);
        
        // Check we found it
        if(type.texture == null)
        {
            throw new IOException("Unable to load tile type '" + type.name + "', unable to find texture '" + textureName + "'");
        }
        
        // Parse properties
        JSONObject props = (JSONObject) obj.get("properties");
        type.properties.parse(props);
        
        return type;
    }

    @Override
    public String toString()
    {
        return "[id: " + id + ", name: " + name + ", texture: " +
                texture.id + ", properties: " + properties + "]";
    }
}
