package com.limpygnome.projectsandbox.server.players;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to store key-value data, for a player, persisted beyond the session.
 */
public class PlayerData<T extends Serializable>
{
    private UUID playerId;
    private Map<String, T> data;

    private PlayerData(UUID playerId)
    {
        this.playerId = playerId;
        this.data = new HashMap<>();
    }

    public synchronized boolean contains(String key)
    {
        return data.containsKey(key);
    }

    public synchronized void put(String key, T value)
    {
        data.put(key, value);
    }

    public synchronized T value(String key)
    {
        return data.remove(key);
    }

    public static PlayerData load(UUID playerId)
    {
        return new PlayerData(playerId);
    }
}
