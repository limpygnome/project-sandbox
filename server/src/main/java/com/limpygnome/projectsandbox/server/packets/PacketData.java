package com.limpygnome.projectsandbox.server.packets;

import com.limpygnome.projectsandbox.server.packets.datatypes.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 28/05/15.
 */
public class PacketData
{
    private List<AbstractDataType> data;
    private int totalBytes;

    public PacketData()
    {
        this.data = new LinkedList<>();
        this.totalBytes = 0;
    }

    public void add(byte[] value)
    {
        add(new ByteArrayDataType(value));
    }

    public void add(byte value)
    {
        add(new ByteDataType(value));
    }

    public void add(float value)
    {
        add(new FloatDataType(value));
    }

    public void add(int value)
    {
        add(new Int32DataType(value));
    }

    public void add(short value)
    {
        add(new ShortDataType(value));
    }

    /**
     * This will only add the long as a 32-bit number.
     *
     * @param value
     */
    public void add(long value)
    {
        add(new Long32DataType(value));
    }

    public void add(String value)
    {
        add(new StringDataType(value));
    }

    public void add(AbstractDataType abstractDataType)
    {
        data.add(abstractDataType);
        totalBytes += abstractDataType.getByteLength();
    }

    /**
     * Builds the packet data into a byte array.
     *
     * @return The compiled byte array.
     */
    public byte[] build()
    {
        ByteBuffer buffer = ByteBuffer.allocate(totalBytes);

        for (AbstractDataType abstractDataType : data)
        {
            abstractDataType.write(buffer);
        }

        return buffer.array();
    }

    public int getSize()
    {
        return data.size();
    }

    public int getBytes()
    {
        return totalBytes;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("[total bytes: ").append(totalBytes).append(", objects: \n");

        for (AbstractDataType abstractDataType : data)
        {
            sb.append("- ").append(abstractDataType).append("\n");
        }
        sb.append("]");

        return sb.toString();
    }
}
