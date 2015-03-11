package com.limpygnome.projectsandbox.textures;

import com.limpygnome.projectsandbox.Constants;
import com.limpygnome.projectsandbox.packets.outbound.TextureDataPacket;
import com.limpygnome.projectsandbox.utils.FileSystem;
import com.limpygnome.projectsandbox.utils.FileSystemFile;
import com.limpygnome.projectsandbox.utils.JsonHelper;
import java.io.IOException;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author limpygnome
 */
public class TextureManager
{
    public HashMap<String, TextureSrc> srcFiles;
    public HashMap<String, Texture> textureFiles;
    
    private short idCounterSrc;
    private short idCounterTexture;
    
    public TextureDataPacket texturePacket;
    
    public TextureManager()
    {
        srcFiles = new HashMap<>();
        textureFiles = new HashMap<>();
        idCounterSrc = 0;
        idCounterTexture = 0;
        texturePacket = null;
    }
    
    public synchronized void load() throws IOException
    {
        // Load src files
        loadSrcFiles();
        
        // Load texture files
        loadTextureFiles();
        
        // Build texture data packet to send to clients
        texturePacket = new TextureDataPacket();
        texturePacket.build(this);
    }
    
    private synchronized void loadSrcFiles() throws IOException
    {
        FileSystemFile[] files = FileSystem.getResources(Constants.BASE_PACKAGE_TEXTURE_SRC);
        
        TextureSrc src;
        JSONObject obj;
        for(FileSystemFile file : files)
        {
            // Load src
            obj = JsonHelper.read(file.getInputStream());
            src = TextureSrc.load(obj);
            src.id = idCounterSrc++;
            
            // Map for textures
            srcFiles.put(src.name, src);
            
            System.out.println("Texture manager - loaded src - " + src);
        }
    }
    
    private synchronized void loadTextureFiles() throws IOException
    {
        FileSystemFile[] files = FileSystem.getResources(Constants.BASE_PACKAGE_TEXTURES);
        
        Texture texture;
        JSONObject obj;
        for(FileSystemFile file : files)
        {
            // Load src
            obj = JsonHelper.read(file.getInputStream());
            texture = Texture.load(this, obj);
            texture.id = idCounterTexture++;
            
            // Map for textures
            textureFiles.put(texture.name, texture);
            
            System.out.println("Texture manager - loaded texture - " + texture);
        }
    }
    
}
