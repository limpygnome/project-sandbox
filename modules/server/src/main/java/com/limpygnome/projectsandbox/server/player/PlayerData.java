package com.limpygnome.projectsandbox.server.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to store key-value data, for a player, which is persisted beyond the session for registered players.
 */
public class PlayerData
{
    private UUID registeredPlayerId;
    private Map<String, Serializable> data;

    private PlayerData(UUID registeredPlayerId)
    {
        this.registeredPlayerId = registeredPlayerId;
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

    /**
     * Persists the player's data.
     */
    public void persist()
    {
        if (registeredPlayerId != null)
        {
            // TODO: persist player data
        }
    }

    /**
     * Loads data for a player.
     *
     * @param registeredPlayerId The registered player's ID; can be null for a guest.
     * @return An instance.
     */
    public static PlayerData load(UUID registeredPlayerId)
    {
        PlayerData playerData = new PlayerData(registeredPlayerId);

        if (registeredPlayerId != null)
        {
            // TODO: load previously persisted data
        }

        return playerData;
    }
}
