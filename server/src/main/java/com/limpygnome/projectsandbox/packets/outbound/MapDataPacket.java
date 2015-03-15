package com.limpygnome.projectsandbox.packets.outbound;

import com.limpygnome.projectsandbox.packets.OutboundPacket;
import com.limpygnome.projectsandbox.world.Map;
import com.limpygnome.projectsandbox.world.TileType;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An outbound packet containing a snapshot of all the data needed to render the
 * map by the client.
 * 
 * TODO: somehow update only certain bytes without remaking the entire packet.
 * 
 * @author limpygnome
 */
public class MapDataPacket extends OutboundPacket
{
    public MapDataPacket()
    {
        super((byte)'M', (byte)'D');
    }
    
    public void build(Map map) throws IOException
    {
        ByteBuffer bb;
        
        // Write number of tiles
        bb = ByteBuffer.allocate(2);
        bb.putShort((short) map.tileTypeMappings.size());
        buffer.write(bb.array());
        
        // Write tile types
        TileType tileType;
        byte[] textureNameBytes;
        for (int i = 0; i < map.tileTypes.length; i++)
        {
            // Fetch tile type
            tileType = map.tileTypes[i];
            
            // Convert texture name to bytes
            textureNameBytes = tileType.texture.getBytes("UTF-8");
            
            // Allocate buffer dynamically for type
            bb = ByteBuffer.allocate(2 + 1 + textureNameBytes.length);
            
            // Write data
            bb.putShort(tileType.id);
            bb.put((byte) tileType.texture.length());
            bb.put(textureNameBytes);
            
            // Add data to packet buffer
            buffer.write(bb.array());
        }
        
        // Write tilesize, w, h
        bb = ByteBuffer.allocate(8);
        bb.putShort(map.id);
        bb.putShort(map.tileSize);
        bb.putShort(map.width);
        bb.putShort(map.height);
        buffer.write(bb.array());
        
        // Write tiles
        bb = ByteBuffer.allocate(map.width * map.height * 2);
        short[] tileRow;
        for(int y = 0; y < map.tiles.length; y++)
        {
            tileRow = map.tiles[y];
            for(int x = 0; x < tileRow.length; x++)
            {
                bb.putShort(tileRow[x]);
            }
        }
        buffer.write(bb.array());
    }
}
