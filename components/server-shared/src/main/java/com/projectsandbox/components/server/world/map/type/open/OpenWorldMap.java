package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.world.map.WorldMap;

import java.io.IOException;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    private static final long serialVersionUID = 1L;

    public OpenWorldMap(short mapId)
    {
        super(mapId);
        this.properties = new OpenWorldMapProperties();
    }

    @Override
    public void rebuildMapPacket() throws IOException
    {
        OpenMapDataOutboundPacket packet = new OpenMapDataOutboundPacket();
        packet.build(this);

        this.packet = packet;
    }

    @Override
    public float getMaxX()
    {
        OpenWorldMapProperties properties = (OpenWorldMapProperties) this.properties;
        return properties.getLimitWidth();
    }

    @Override
    public float getMaxY()
    {
        OpenWorldMapProperties properties = (OpenWorldMapProperties) this.properties;
        return properties.getLimitHeight();
    }

}
