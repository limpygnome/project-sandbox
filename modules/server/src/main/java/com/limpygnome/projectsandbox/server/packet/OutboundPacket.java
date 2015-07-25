package com.limpygnome.projectsandbox.server.packet;

import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

/**
 *
 * @author limpygnome
 */
public abstract class OutboundPacket extends Packet
{
    private final static Logger LOG = LogManager.getLogger(OutboundPacket.class);

    protected PacketData packetData;
    byte[] dataCached;

    public OutboundPacket()
    {
        this.packetData = new PacketData();
        this.dataCached = null;
    }

    public OutboundPacket(byte mainType)
    {
        this();

        packetData.add(mainType);
    }

    public OutboundPacket(byte mainType, byte subType)
    {
        this();
        
        // Write header data
        packetData.add(mainType);
        packetData.add(subType);
    }

    public PacketData getPacketData()
    {
        return packetData;
    }

    public void send(PlayerInfo player)
    {
        // Build data and cache it, if not already cached
        if (dataCached == null)
        {
            dataCached = packetData.build();
        }

        // Check socket not closed
        if (!player.socket.isClosed())
        {
            try
            {
                player.socket.send(dataCached);
            }
            catch (WebsocketNotConnectedException e)
            {
                LOG.error("Failed to send data to player", e);
            }
        }
    }
}
