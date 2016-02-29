package com.limpygnome.projectsandbox.server.packet.datatype;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 28/05/15.
 */
public class StringDataType implements AbstractDataType
{

    private StringCharSize lengthSize;
    private byte[] data;

    public StringDataType(String value, StringCharSize lengthSize)
    {
        try
        {
            this.data = value.getBytes("UTF-8");
            this.lengthSize = lengthSize;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 encoding not available", e);
        }
    }

    @Override
    public int getByteLength()
    {
        switch (lengthSize)
        {
            case LENGTH_8_BITS:
                return 1 + data.length;
            case LENGTH_16_BITS:
                return 2 + data.length;
            default:
                throw new RuntimeException("Unhandled string length size");
        }
        // We'll use one byte for the length of the data
    }

    @Override
    public void write(ByteBuffer buffer)
    {
        // Write length of data - varies for length size
        switch (lengthSize)
        {
            case LENGTH_8_BITS:
                buffer.put((byte) data.length);
                break;
            case LENGTH_16_BITS:
                buffer.putShort((short) data.length);
                break;
            default:
                throw new RuntimeException("Unhandled string length size");
        }

        // Write data
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
