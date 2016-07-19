package com.projectsandbox.components.server.world.map.type.open;

import com.projectsandbox.components.server.network.packet.OutboundPacket;

import java.io.IOException;

/**
 * Packet data for an implementation of {@link OpenWorldMap}, intended to be cached and sent to multiple users
 * once it has been built.
 */
public class OpenMapDataOutboundPacket extends OutboundPacket
{

    public OpenMapDataOutboundPacket()
    {
        super((byte)'M', (byte)'O');
    }

    public void build(OpenWorldMap map) throws IOException
    {
        OpenWorldMapProperties properties = (OpenWorldMapProperties) map.getProperties();

        // Write limits of map
        packetData.add(properties.getLimitWidth());
        packetData.add(properties.getLimitHeight());

        // Write background data
        String background = properties.getBackground();

        // -- 0: Flag to indicate if background enabled
        // -- 1: texture (string), if enabled is true

        if (background != null && background.length() > 0)
        {
            packetData.add(true);
            packetData.addAscii(background);
        }
        else
        {
            packetData.add(false);
        }
    }

}
