package com.limpygnome.projectsandbox.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author limpygnome
 */
public class ByteHelper
{
    public static String debug(byte[] data)
    {
        return Arrays.toString(data) + " (len: " + data.length + ")";
    }
    
    public static String debug(List<Object> objs)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("len: ").append(objs.size()).append(" - [\n");
        for (Object obj : objs)
        {
            sb.append(obj.getClass()).append(" : ").append(obj).append("\n");
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    public static byte[] convertListOfObjects(List<Object> objs)
    {
        // Compute size of array
        int size = 0;
        for (Object o : objs)
        {
            if (o instanceof Float || o instanceof Integer)
            {
                size += 4;
            }
            else if (o instanceof Short)
            {
                size += 2;
            }
            else if (o instanceof Byte || o instanceof Character)
            {
                size += 1;
            }
            else if (o instanceof byte[])
            {
                size += ( (byte[]) o).length;
            }
            else
            {
                throw new IllegalArgumentException("Unsupported byte object - " + o.getClass().getName());
            }
        }
        
        // Compile into buffer
        ByteBuffer bb = ByteBuffer.allocate(size);
        for (Object o : objs)
        {
            if (o instanceof Float)
            {
                bb.putFloat((float) o);
            }
            else if (o instanceof Integer)
            {
                bb.putInt((int) o);
            }
            else if (o instanceof Short)
            {
                bb.putShort((short) o);
            }
            else if (o instanceof Byte || o instanceof Character)
            {
                bb.put((byte) o);
            }
            else if (o instanceof byte[])
            {
                bb.put((byte[]) o);
            }
        }
        
        return bb.array();
    }
    
}
