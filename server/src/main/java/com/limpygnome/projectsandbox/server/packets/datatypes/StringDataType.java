package com.limpygnome.projectsandbox.server.packets.datatypes;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by limpygnome on 28/05/15.
 */
public class StringDataType implements AbstractDataType
{
    private byte[] data;

    public StringDataType(String value)
    {
        try
        {
            data = value.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 encoding not available", e);
        }
    }

    @Override
    public int getByteLength()
    {
        // We'll use one byte for the length of the data
        return 1 + data.length;
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        buffer.put((byte) data.length);
        buffer.put(data);
    }

    @Override
    public String toString()
    {
        try
        {
            return "[string (UTF-8) : " + (1 + data.length) + " : " + new String(data, "UTF-8") + "]";
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 encoding not available", e);
        }
    }
}
