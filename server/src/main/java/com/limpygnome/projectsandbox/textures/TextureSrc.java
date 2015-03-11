package com.limpygnome.projectsandbox.textures;

import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class TextureSrc
{
    // auto-gen id by texturemanager
    public short id;
    
    public String name;
    public short width;
    public short height;
    public String url;
    public String description;
    
    public static TextureSrc load(JSONObject obj) throws IOException
    {
        TextureSrc ts = new TextureSrc();
        
        ts.name = (String) obj.get("name");
        ts.width = (short) (long) obj.get("width");
        ts.height = (short) (long) obj.get("height");
        ts.url = (String) obj.get("url");
        ts.description =  (String) obj.get("description");
        
        return ts;
    }

    @Override
    public String toString()
    {
        return "[id: " + id + ", name: " + name + ", w: " + width + ", h: " +
                height + ", url: " + url + ", desc: " + description + "]";
    }
}
