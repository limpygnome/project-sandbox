package com.projectsandbox.components.server.world.map.type.tile;

import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * A world map where entities move along tiles.
 */
public class TileWorldMap extends WorldMap
{
    private final static Logger LOG = LogManager.getLogger(TileWorldMap.class);

    /**
     * Tile data for this map.
     */
    public TileData tileData;

    public TileWorldMap(short mapId)
    {
        super(mapId);
        this.properties = new WorldMapProperties();
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
        return tileData.maxX;
    }

    @Override
    public float getMaxY()
    {
        return tileData.maxY;
    }

}
