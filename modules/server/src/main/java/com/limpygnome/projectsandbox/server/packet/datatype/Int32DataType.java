package com.limpygnome.projectsandbox.server.packet.datatype;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 28/05/15.
 */
public class Int32DataType implements AbstractDataType
{
    private int value;

    public Int32DataType(int value)
    {
        this.value = value;
    }

    @Override
    public int getByteLength()
    {
        return 4;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.putInt(value);
    }

    @Override
    public String toString()
    {
        return "[int : " + 4 + " : " + value + "]";
    }
}
