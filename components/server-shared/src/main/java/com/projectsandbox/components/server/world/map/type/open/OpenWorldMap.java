package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    private final static Logger LOG = LogManager.getLogger(OpenWorldMap.class);

    public OpenWorldMap(Controller controller, MapService mapService, short mapId)
    {
        super(controller, mapService, mapId);
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
    public void setProperties(WorldMapProperties properties)
    {
        super.setProperties(properties);

        this.properties = (OpenWorldMapProperties) properties;
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
