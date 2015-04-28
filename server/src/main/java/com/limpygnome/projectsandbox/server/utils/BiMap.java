package com.limpygnome.projectsandbox.server.utils;

import java.util.HashMap;

/**
 * TODO: consider deletion
 * 
 * @author limpygnome
 */
public class BiMap<K, V>
{
    private HashMap<K, V> mappingKeyToValue;
    private HashMap<V, K> mappingValueToKey;
    
    public BiMap()
    {
        this.mappingKeyToValue = new HashMap<>();
        this.mappingValueToKey = new HashMap<>();
    }
    
    public void put(K key, V value)
    {
        // Put new values
        mappingKeyToValue.put(key, value);
        mappingValueToKey.put(value, key);
    }
    
    public V getByKey(K key)
    {
        return mappingKeyToValue.get(key);
    }
    
    public K getByValue(V value)
    {
        return mappingValueToKey.get(value);
    }
}
