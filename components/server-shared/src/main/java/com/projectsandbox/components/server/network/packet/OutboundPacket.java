package com.projectsandbox.components.server.network.packet;

/**
 * Used to build and hold cached packet data to be sent to a player.
 */
public abstract class OutboundPacket extends Packet
{
    protected PacketData packetData;
    private byte[] dataCached;

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

    /**
     * Builds and returns the packet data.
     *
     * On first invocation, the data is cached, so that subsequent calls do not rebuild the packet.
     *
     * @return the packet data
     */
    public byte[] build()
    {
        // Only build if not cached already...
        if (dataCached == null)
        {
            dataCached = packetData.build();
        }

        return dataCached;
    }

}
