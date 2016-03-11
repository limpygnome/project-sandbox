package com.limpygnome.projectsandbox.server.world.map.open;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.packet.OpenMapDataOutboundPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * An open space map, suitable for space.
 */
public class OpenWorldMap extends WorldMap
{
    private final static Logger LOG = LogManager.getLogger(OpenWorldMap.class);

    private OpenWorldMapProperties properties;

    public OpenWorldMap(Controller controller, MapService mapService, short mapId)
    {
        super(controller, mapService, mapId);
    }

    @Override
    public void rebuildMapPacket() throws IOException
    {
        OpenMapDataOutboundPacket packet = new OpenMapDataOutboundPacket();
        packet.build(this);

        this.packet = packet;
    }

    @Override
    public OpenWorldMapProperties getProperties()
    {
        return properties;
    }

}
