package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    @Autowired
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

    public OpenWorldMapData getOpenWorldMapData()
    {
        return openWorldMapData;
    }

    @Override
    public List<MapData> getMapData()
    {
        List<MapData> result = super.getMapData();
        result.add(openWorldMapData);
        return result;
    }

}
