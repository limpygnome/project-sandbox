package com.projectsandbox.components.server.world.map.type.tile;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.GeneralMapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * A world map where entities move along tiles.
 */
public class TileWorldMap extends WorldMap
{
    /**
     * Tile data for this map.
     */
    @Autowired
    public TileMapData tileMapData;

    public TileWorldMap(String mapId, Controller controller)
    {
        super(mapId, controller);
    }

    @Override
    public void rebuildMapPacket() throws IOException
    {
        TileMapDataOutboundPacket packet = new TileMapDataOutboundPacket();
        packet.build(this);

        this.packet = packet;
    }

    @Override
    public float getMaxX()
    {
        return tileMapData.maxX;
    }

    @Override
    public float getMaxY()
    {
        return tileMapData.maxY;
    }

}
