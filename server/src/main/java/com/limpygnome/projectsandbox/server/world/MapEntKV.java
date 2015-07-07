package com.limpygnome.projectsandbox.server.world;

import java.util.HashMap;

/**
 * Created by limpygnome on 07/07/15.
 */
public class MapEntKV extends HashMap<String, String>
{
    @Override
    public String get(Object key)
    {
        String v = super.get(key);

        if (v == null)
        {
            throw new RuntimeException("Key not found in KV - key: " + key);
        }

        return v;
    }

    public long getLong(String key)
    {
        String v = get(key);

        try
        {
            return Long.parseLong(v);
        }
        catch (NumberFormatException e)
        {
            throw new RuntimeException("Invalid KV value for type long - key: " + key);
        }
    }

    public float getFloat(String key)
    {
        String v = get(key);

        try
        {
            return Float.parseFloat(key);
        }
        catch (NumberFormatException e)
        {
            throw new RuntimeException("Invalid KV value for type float - key: " + key);
        }
    }
}
