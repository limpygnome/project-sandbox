package com.limpygnome.projectsandbox.server.world.map.tile;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.packet.TileMapDataOutboundPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by limpygnome on 09/03/16.
 */
public class TileWorldMap extends WorldMap
{
    private final static Logger LOG = LogManager.getLogger(TileWorldMap.class);

    public TileWorldMap(Controller controller, MapService mapService, short mapId)
    {
        super(controller, mapService, mapId);
    }

    @Override
    public void rebuildMapPacket() throws IOException
    {
        TileMapDataOutboundPacket packet = new TileMapDataOutboundPacket();
        packet.build(this);

        this.packet = packet;
    }

}
