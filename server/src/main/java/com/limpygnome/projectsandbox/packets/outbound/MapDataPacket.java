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
        bb = ByteBuffer.allocate(4);
        
        TileType tileType;
        for (int i = 0; i < map.tileTypes.length; i++)
        {
            tileType = map.tileTypes[i];
            bb.putShort(tileType.id);
            bb.putShort(tileType.texture.id);
            buffer.write(bb.array());
            bb.clear();
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
