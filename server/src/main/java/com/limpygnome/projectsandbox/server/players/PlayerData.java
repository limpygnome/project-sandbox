package com.limpygnome.projectsandbox.server.players;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to store key-value data, for a player, persisted beyond the session.
 */
public class PlayerData
{
    private UUID playerId;
    private Map<String, Serializable> data;

    private PlayerData(UUID playerId)
    {
        this.playerId = playerId;
        this.data = new HashMap<>();
    }

    public synchronized boolean contains(String key)
    {
        return data.containsKey(key);
    }

    private synchronized Serializable get(String key, Serializable defaultValue)
    {
        Serializable v = data.get(key);

        if (v == null)
        {
            v = defaultValue;
            data.put(key, v);
        }

        return v;
    }

    public synchronized Serializable get(String key)
    {
        return get(key, null);
    }

    public synchronized long getLong(String key)
    {
        return (Long) get(key, 0L);
    }

    public synchronized int getInt(String key)
    {
        return (Integer) get(key, 0);
    }

    public synchronized String getStr(String key)
    {
        return (String) get(key, "");
    }

    // TODO: implement more of the above, plus equivalent for put

    public synchronized void put(String key, Serializable value)
    {
        data.put(key, value);
    }

    public synchronized Serializable remove(String key)
    {
        return data.remove(key);
    }

    public static PlayerData load(UUID playerId)
    {
        return new PlayerData(playerId);
    }
}
