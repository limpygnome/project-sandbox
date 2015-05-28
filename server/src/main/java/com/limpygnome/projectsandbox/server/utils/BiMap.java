package com.limpygnome.projectsandbox.server.utils;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A bi-directional hash map, which works by containing two maps with key-values going either way. Therefore
 * the K and V must be one-to-one.
 * 
 * @author limpygnome
 */
public class BiMap<K, V> implements Iterable
{
    private HashMap<K, V> mappingKeyToValue;
    private HashMap<V, K> mappingValueToKey;
    
    public BiMap()
    {
        this.mappingKeyToValue = new HashMap<>();
        this.mappingValueToKey = new HashMap<>();
    }
    
    public synchronized void put(K key, V value)
    {
        // Put new values
        mappingKeyToValue.put(key, value);
        mappingValueToKey.put(value, key);
    }
    
    public synchronized V getByKey(K key)
    {
        return mappingKeyToValue.get(key);
    }
    
    public synchronized K getByValue(V value)
    {
        return mappingValueToKey.get(value);
    }

    @Override
    public synchronized Iterator iterator()
    {
        return mappingKeyToValue.entrySet().iterator();
    }
}
