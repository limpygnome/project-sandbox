package com.projectsandbox.components.server.network.packet.datatype;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 28/05/15.
 */
public class ByteDataType implements AbstractDataType
{
    private byte value;

    public ByteDataType(byte value)
    {
        this.value = value;
    }

    @Override
    public int getByteLength()
    {
        return 1;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.put(value);
    }

    @Override
    public String toString()
    {
        return "[byte : 1 : " + value + "]";
    }
}
