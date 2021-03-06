package com.projectsandbox.components.server.entity.component;

import java.io.Serializable;
import java.util.*;

/**
 * Used to hold a collection of component callbacks.
 */
public class ComponentCollection< K extends Class<ComponentEvent>, V extends EntityComponent> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final Map<K, HashSet<V>> eventCallbacks;
    private final Map<Class<? extends EntityComponent>, V> components;

    public ComponentCollection()
    {
        eventCallbacks = new HashMap<>();
        components = new HashMap<>();
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

    public synchronized V fetchComponent(Class<V> clazz)
    {
        return components.get(clazz);
    }

    public synchronized ComponentCollection add(V entityComponent)
    {
        // Register entity for each event type for callback
        Class[] clazzes = entityComponent.getClass().getInterfaces();

        for (Class clazz : clazzes)
        {
            if (ComponentEvent.class.isAssignableFrom(clazz))
            {
                registerEventType((K) clazz, entityComponent);
            }
        }

        // Add to components
        components.put(entityComponent.getClass(), entityComponent);

        return this;
    }

    public synchronized V remove(Class<V> clazz)
    {
        // Fetch component
        V entityComponent = fetchComponent(clazz);

        if (entityComponent != null)
        {
            remove(entityComponent);
        }

        return entityComponent;
    }

    public synchronized V remove(V entityComponent)
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

        // Remove from components
        components.remove(entityComponent.getClass());

        return entityComponent;
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
