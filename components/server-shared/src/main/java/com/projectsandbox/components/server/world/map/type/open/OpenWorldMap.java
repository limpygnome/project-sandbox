package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.MapType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * An open space map, suitable for space.
 */
@MapType(typeName = "open-world-map", classType = OpenWorldMap.class)
public class OpenWorldMap extends WorldMap
{
    @Autowired
    private OpenWorldMapData openWorldMapData;

    public OpenWorldMap(String mapId, Controller controller)
    {
        super(mapId, controller);

        // Add map data
        mapData.add(openWorldMapData);
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

    public OpenWorldMapData getOpenWorldMapData()
    {
        return openWorldMapData;
    }

}
