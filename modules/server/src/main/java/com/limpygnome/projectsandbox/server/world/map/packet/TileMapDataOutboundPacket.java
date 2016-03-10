package com.limpygnome.projectsandbox.server.world.map.packet;

import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.tile.TileType;
import com.limpygnome.projectsandbox.server.packet.OutboundPacket;

import java.io.IOException;

/**
 * An outbound packet containing a snapshot of all the data needed to render the
 * map by the client.
 * 
 * TODO: somehow update only certain bytes without remaking the entire packet.
 */
public class TileMapDataOutboundPacket extends OutboundPacket
{
    public TileMapDataOutboundPacket()
    {
        super((byte)'M', (byte)'T');
    }
    
    public void build(WorldMap map) throws IOException
    {
        // Add number of tiles
        packetData.add((short) map.tileData.tileTypes.length);
        
        // Add tile imp
        TileType tileType;
        byte[] textureNameBytes;

        for (int i = 0; i < map.tileData.tileTypes.length; i++)
        {
            // Fetch tile type
            tileType = map.tileData.tileTypes[i];
            
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
        packetData.add((short) map.tileData.tileSize);
        packetData.add(map.tileData.widthTiles);
        packetData.add(map.tileData.heightTiles);
        
        // Add tiles
        short[] tileRow;
        for(int y = 0; y < map.tileData.tiles.length; y++)
        {
            tileRow = map.tileData.tiles[y];
            for(int x = 0; x < tileRow.length; x++)
            {
                packetData.add(tileRow[x]);
            }
        }
    }

}
