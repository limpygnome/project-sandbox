package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.io.IOException;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    private OpenWorldMapData openWorldMapData;

    public OpenWorldMap(String mapId, Controller controller)
    {
        super(mapId, controller);
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
        return openWorldMapData.getLimitWidth();
    }

    @Override
    public float getMaxY()
    {
        return openWorldMapData.getLimitHeight();
    }

}
