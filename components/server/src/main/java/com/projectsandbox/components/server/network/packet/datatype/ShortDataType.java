package com.projectsandbox.components.server.network.packet.datatype;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 28/05/15.
 */
public class ShortDataType implements AbstractDataType
{
    private short value;

    public ShortDataType(short value)
    {
        this.value = value;
    }

    @Override
    public int getByteLength()
    {
        return 2;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.putShort(value);
    }

    @Override
    public String toString()
    {
        return "[short : " + 2 + " : " + value + "]";
    }
}
