package com.limpygnome.projectsandbox.server.entity.component;

import java.util.*;

/**
 * Created by limpygnome on 08/04/16.
 */
public class ComponentEventHandler<K extends Class<ComponentEvent>, V extends EntityComponent, VT extends Class<V>>
{
    private Map<K, HashSet<V>> eventCallbacks;

    public ComponentEventHandler()
    {
        eventCallbacks = new HashMap<>();
    }

    public synchronized Set<V> fetch(K clazz)
    {
        return eventCallbacks.get(clazz);
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

        while (iterator.hasNext())
        {
            kv = iterator.next();
            callbacks = kv.getValue();

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
