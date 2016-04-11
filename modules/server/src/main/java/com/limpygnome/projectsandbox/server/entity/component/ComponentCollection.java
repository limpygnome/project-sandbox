package com.limpygnome.projectsandbox.server.entity.component;

import java.util.*;

/**
 * Used to hold a collection of component callbacks.
 */
public class ComponentCollection< K extends Class<ComponentEvent>, V extends EntityComponent>
{
    private final Map<K, HashSet<V>> eventCallbacks;

    public ComponentCollection()
    {
        eventCallbacks = new HashMap<>();
    }

    /**
     * Fetches list of components registered to class event.
     *
     * @param clazz
     * @return always returns result, may be empty if no components registered
     */
    public synchronized Set<V> fetch(K clazz)
    {
        Set<V> result = eventCallbacks.get(clazz);

        if (result == null)
        {
            result = Collections.emptySet();
        }

        return result;
    }

    public synchronized V fetchSingle(K clazz)
    {
        Set<V> result = fetch(clazz);
        V scalarResult = result.iterator().next();
        return scalarResult;
    }

    public synchronized void register(V entityComponent)
    {

        // Register entity for each event type
        Class[] clazzes = entityComponent.getClass().getClasses();

        for (Class clazz : clazzes)
        {
            if (clazz.isAssignableFrom(ComponentEvent.class))
            {
                registerEventType((K) clazz, entityComponent);
            }
        }
    }

    public synchronized void unregister(V entityComponent)
    {
        Set<Map.Entry<K, HashSet<V>>> set = eventCallbacks.entrySet();
        Iterator<Map.Entry<K, HashSet<V>>> iterator = set.iterator();
        Map.Entry<K, HashSet<V>> kv;
        HashSet<V> callbacks;

        // Remove callbacks for component; quicker just to iterate all KVs than classes and fetching
        while (iterator.hasNext())
        {
            kv = iterator.next();
            callbacks = kv.getValue();

            // Remove component and then entire list if empty
            if (callbacks.remove(entityComponent) && callbacks.isEmpty())
            {
                iterator.remove();
            }
        }
    }

    public synchronized void registerEventType(K componentEvent, V entityComponent)
    {
        // Fetch list of callbacks
        HashSet<V> callbacks = eventCallbacks.get(componentEvent);

        if (callbacks == null)
        {
            callbacks = new HashSet<>();
            eventCallbacks.put(componentEvent, callbacks);
        }

        callbacks.add(entityComponent);
    }

}
