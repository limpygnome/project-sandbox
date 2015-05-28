package com.limpygnome.projectsandbox.server.packets;

import com.limpygnome.projectsandbox.server.players.PlayerInfo;

/**
 *
 * @author limpygnome
 */
public abstract class OutboundPacket extends Packet
{
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
        if (dataCached == null)
        {
            dataCached = packetData.build();
        }
        player.socket.send(dataCached);
    }
}
