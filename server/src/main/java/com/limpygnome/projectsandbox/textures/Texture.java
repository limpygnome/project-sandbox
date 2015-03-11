package com.limpygnome.projectsandbox.textures;

import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class Texture
{
    // auto-gen id by texturemanager
    public short id;
    
    public String name;
    public short speed;
    public float[][] frameVertices;
    public TextureSrc src;
    
    // computed at load-time
    public short frames;
    
    public static Texture load(TextureManager textureManager, JSONObject obj) throws IOException
    {
        Texture texture = new Texture();
        
        // Load main attributes
        texture.name = (String) obj.get("name");
        texture.speed = (short) (long) obj.get("speed");
        
        // Lookup src
        String src = (String) obj.get("src");
        texture.src = textureManager.srcFiles.get(src);
        
        // Check src has been found
        if(texture.src == null)
        {
            throw new IOException("Cannot map src '" + src + "' for texture " + texture.name);
        }
        
        // Load frames
        JSONArray frames = (JSONArray) obj.get("frames");
        
        // Setup frames arr
        texture.frames = (short) frames.size();
        texture.frameVertices = new float[texture.frames][];
        
        // Build each frame from x,y to unit vector
        JSONObject frame;
        for(int i = 0; i < texture.frames; i++)
        {
            frame = (JSONObject) frames.get(i);
            
            // Build frame
            texture.frameVertices[i] = buildFrame(frame, texture.src.width, texture.src.height);
        }
        
        return texture;
    }
    
    private static float[] buildFrame(JSONObject frame, short width, short height) throws IOException
    {
        float[] vertices = new float[4 * 2]; // 4 sets of xy (2)
        
        // Build so we have: bl -> br -> tr -> tl
        
        String tl = (String) frame.get("tl");
        buildFrameVertices(tl, vertices, 6, width, height);
        
        String tr = (String) frame.get("tr");
        buildFrameVertices(tr, vertices, 4, width, height);
        
        String bl = (String) frame.get("bl");
        buildFrameVertices(bl, vertices, 0, width, height);
        
        String br = (String) frame.get("br");
        buildFrameVertices(br, vertices, 2, width, height);
        
        return vertices;
    }
    
    private static void buildFrameVertices(String vector, float[] vertices, int offset, float width, float height) throws IOException
    {
        // Split vector into vertices
        String[] verts = vector.split(",");
        if(verts.length != 2)
        {
            throw new IOException("Invalid verts length for '" + vector + "'");
        }
        
        // Parse as actual floats
        vertices[offset] = Float.parseFloat(verts[0].trim());
        vertices[offset + 1] = Float.parseFloat(verts[1].trim());
        
        // Convert to unit vector
        vertices[offset] = vertices[offset] / width;
        vertices[offset + 1] = vertices[offset + 1] / height;
        
        // Check value ranges are valid
        if(vertices[offset] < 0 || vertices[offset] > 1)
        {
            throw new IOException("Invalid vector " + vector + " - uv.x: " + vertices[offset]);
        }
        else if(vertices[offset + 1] < 0 || vertices[offset + 1] > 1)
        {
            throw new IOException("Invalid vector " + vector + " - uv.y: " + vertices[offset + 1]);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[\n");

        // Main attributes
        sb.append("\tid:\t").append(id).append("\n");
        sb.append("\tname:\t").append(name).append("\n");
        sb.append("\tspeed:\t").append(speed).append("\n");
        sb.append("\tsrc:\t").append(src.name).append(" [mapped]\n");
        
        // Frames
        sb.append("\tframes:\n");
        for(int i = 0; i < frameVertices.length; i++)
        {
            sb.append("\t\tframe ").append(i).append(":\n\t\t");
            for(int j = 0; j < frameVertices[i].length; j++)
            {
                sb.append("\tv ").append(j).append(": ").append(frameVertices[i][j]);
            }
            sb.append("\n");
        }
        
        sb.append("]");
        
        return sb.toString();
    }
}
