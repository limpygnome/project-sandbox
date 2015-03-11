package com.limpygnome.projectsandbox.packets.outbound;

import com.limpygnome.projectsandbox.packets.OutboundPacket;
import com.limpygnome.projectsandbox.textures.Texture;
import com.limpygnome.projectsandbox.textures.TextureManager;
import com.limpygnome.projectsandbox.textures.TextureSrc;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 * A packet used to send all of the texture data to the client.
 * 
 * @author limpygnome
 */
public class TextureDataPacket extends OutboundPacket
{
    private byte[] data;
    
    public TextureDataPacket()
    {
        super((byte) 'T', (byte) 'D');
        
        data = null;
    }
    
    public void build(TextureManager textureManager) throws IOException
    {
        ByteBuffer bb;
        byte[] tempBuffer;
        
        // Build src
        // -- Put the number of src elements
        {
            bb = ByteBuffer.allocate(2);
            bb.putShort((short) textureManager.srcFiles.size());
            buffer.write(bb.array());
        }
        // -- Iterate and add each element
        TextureSrc src;
        for(Map.Entry<String, TextureSrc> kv : textureManager.srcFiles.entrySet())
        {
            src = kv.getValue();
            
            // Fetch URL as UTF-8
            tempBuffer = src.url.getBytes("UTF-8");
            
            // Allocate buffer - 2 bytes for id, 2 bytes for url length
            bb = ByteBuffer.allocate(4 + tempBuffer.length);
            
            // Write bytes
            bb.putShort(src.id);
            bb.putShort((short) tempBuffer.length);
            bb.put(tempBuffer);
            buffer.write(bb.array());
        }
        
        // Build textures
        // -- Put the number of texture elements
        {
            bb = ByteBuffer.allocate(2);
            bb.putShort((short) textureManager.textureFiles.size());
            buffer.write(bb.array());
        }
        // -- Iterate and add each element
        Texture texture;
        for(Map.Entry<String, Texture> kv : textureManager.textureFiles.entrySet())
        {
            texture = kv.getValue();
            
            // Allocate buffer - 2 (id), 2 (src id), 2 (speed), 2 (frames), frames*8*4
            bb = ByteBuffer.allocate(8 + (texture.frameVertices.length * 8 * 4));
            
            // Write bytes
            bb.putShort(texture.id);
            bb.putShort(texture.src.id);
            bb.putShort(texture.speed);
            bb.putShort(texture.frames);
            
            for(int i = 0; i < texture.frameVertices.length; i++)
            {
                for(int j = 0; j < texture.frameVertices[i].length; j++)
                {
                    bb.putFloat(texture.frameVertices[i][j]);
                }
            }
            
            buffer.write(bb.array());
        }
        
        data = buffer.toByteArray();
        buffer = null;
    }

    @Override
    public byte[] getPacketData()
    {
        return data;
    }
}
