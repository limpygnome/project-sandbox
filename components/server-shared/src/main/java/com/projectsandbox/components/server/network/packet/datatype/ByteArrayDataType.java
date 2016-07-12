package com.projectsandbox.components.server.network.packet.datatype;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by limpygnome on 28/05/15.
 */
public class ByteArrayDataType implements AbstractDataType
{
    private byte[] value;

    public ByteArrayDataType(byte[] value)
    {
        this.value = value;
    }

    @Override
    public int getByteLength()
    {
        return value.length;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.put(value);
    }

    @Override
    public String toString()
    {
        return "[byte array : " + value.length + " : " + Arrays.toString(value) + "]";
    }
}
