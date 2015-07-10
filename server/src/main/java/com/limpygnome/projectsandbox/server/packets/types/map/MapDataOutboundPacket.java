package com.limpygnome.projectsandbox.server.packets.types.map;

import com.limpygnome.projectsandbox.server.world.TileType;
import com.limpygnome.projectsandbox.server.packets.OutboundPacket;
import com.limpygnome.projectsandbox.server.world.Map;

import java.io.IOException;

/**
 * An outbound packet containing a snapshot of all the data needed to render the
 * map by the client.
 * 
 * TODO: somehow update only certain bytes without remaking the entire packet.
 * 
 * @author limpygnome
 */
public class MapDataOutboundPacket extends OutboundPacket
{
    public MapDataOutboundPacket()
    {
        super((byte)'M', (byte)'D');
    }
    
    public void build(Map map) throws IOException
    {
        // Add number of tiles
        packetData.add((short) map.tileNameToTypeIndexMappings.size());
        
        // Add tile types
        TileType tileType;
        byte[] textureNameBytes;

        for (int i = 0; i < map.tileTypes.length; i++)
        {
            // Fetch tile type
            tileType = map.tileTypes[i];
            
            // Convert texture name to bytes
            textureNameBytes = tileType.texture.getBytes("UTF-8");
            
            // Add type data
            packetData.add(tileType.id);
            packetData.add((short) tileType.properties.height);
            packetData.add((byte) tileType.texture.length());
            packetData.add(textureNameBytes);
        }
        
        // Add map properties
        packetData.add(map.mapId);
        packetData.add(map.tileSize);
        packetData.add(map.width);
        packetData.add(map.height);
        
        // Add tiles
        short[] tileRow;
        for(int y = 0; y < map.tiles.length; y++)
        {
            tileRow = map.tiles[y];
            for(int x = 0; x < tileRow.length; x++)
            {
                packetData.add(tileRow[x]);
            }
        }
    }
}
