package com.limpygnome.projectsandbox.server.packets.datatypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by limpygnome on 10/06/15.
 */
public class Long32DataType implements AbstractDataType
{
    private byte[] rawValue;

    public Long32DataType(long value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt((int) value);
        buffer.flip();

        this.rawValue = buffer.array();
    }

    @Override
    public int getByteLength()
    {
        return 4;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.put(rawValue);
    }

    @Override
    public String toString()
    {
        return "[long32 : " + rawValue.length + " : " + Arrays.toString(rawValue) + "]";
    }
}
