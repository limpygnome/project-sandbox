package com.limpygnome.projectsandbox.server.world.map.tile;

import com.limpygnome.projectsandbox.server.Controller;
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
    public String texture;
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
        type.texture = textureName;
        
        // Parse properties
        JSONObject props = (JSONObject) obj.get("properties");
        type.properties.parse(props);
        
        return type;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("id: ").append(id).append(",");
        sb.append("name: ").append(name).append(",");
        sb.append("texture: ").append(texture).append(",");
        sb.append(properties);
        sb.append("}");

        return sb.toString();
    }

}
