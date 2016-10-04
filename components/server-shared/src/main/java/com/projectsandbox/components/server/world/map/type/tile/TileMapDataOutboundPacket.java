package com.projectsandbox.components.server.world.map.type.tile;

import com.projectsandbox.components.server.network.packet.OutboundPacket;

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
    
    public void build(TileWorldMap map) throws IOException
    {
        // Add number of tiles
        packetData.add((short) map.tileMapData.tileTypes.length);
        
        // Add tile imp
        TileType tileType;
        byte[] textureNameBytes;

        for (int i = 0; i < map.tileMapData.tileTypes.length; i++)
        {
            // Fetch tile type
            tileType = map.tileMapData.tileTypes[i];
            
            // Convert texture name to bytes
            textureNameBytes = tileType.texture.getBytes("UTF-8");
            
            // Add type data
            packetData.add(tileType.id);
            packetData.add((short) tileType.properties.height);
            packetData.add((byte) tileType.texture.length());
            packetData.add(textureNameBytes);
        }
        
        // Add map properties
        packetData.add(map.getMapId());
        packetData.add((short) map.tileMapData.tileSize);
        packetData.add(map.tileMapData.widthTiles);
        packetData.add(map.tileMapData.heightTiles);
        
        // Add tiles
        short[] tileRow;
        for(int y = 0; y < map.tileMapData.tiles.length; y++)
        {
            tileRow = map.tileMapData.tiles[y];
            for(int x = 0; x < tileRow.length; x++)
            {
                packetData.add(tileRow[x]);
            }
        }
    }

}
