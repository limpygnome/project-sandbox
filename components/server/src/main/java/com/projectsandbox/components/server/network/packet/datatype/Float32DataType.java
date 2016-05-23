package com.projectsandbox.components.server.network.packet.datatype;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 28/05/15.
 */
public class Float32DataType implements AbstractDataType
{
    private float value;

    public Float32DataType(float value)
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
        buffer.putFloat(value);
    }

    @Override
    public String toString()
    {
        return "[float : " + 4 + " : " + value + "]";
    }
}
