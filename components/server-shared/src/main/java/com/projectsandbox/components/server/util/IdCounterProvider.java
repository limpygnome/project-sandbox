package com.projectsandbox.components.server.util;

import com.projectsandbox.components.server.util.counters.IdCounterConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * Provides 16-bit identifiers, used for Entity and PlayerInfo IDs.
 */
public class IdCounterProvider implements Serializable
{
    private final static long serialVersionUID = 1L;
    private final static Logger LOG = LogManager.getLogger(IdCounterProvider.class);

    // The consumer of these identifiers; used to check if it already contains an identifier
    private IdCounterConsumer consumer;

    // Keeps track of what is most likely the next available ID
    private short nextId;

    public IdCounterProvider(IdCounterConsumer consumer)
    {
        this.nextId = 0;
        this.consumer = consumer;
    }

    public synchronized Short nextId(Short currentId)
    {
        short id;

        synchronized (consumer)
        {
            // Check if current id is available
            if (currentId != null && !consumer.containsId(currentId))
            {
                id = currentId;
            }
            else
            {

                // Find next available ID
                boolean foundNewId = false;
                int attempts = 0;

                do
                {
                    id = nextId++;
                    if (!consumer.containsId(id))
                    {
                        foundNewId = true;
                    }
                }
                while (!foundNewId && ++attempts < Short.MAX_VALUE);

                // Check we found an identifier
                if (!foundNewId)
                {
                    LOG.warn("Available IDs, for consumer {}, has been depleted", consumer.getClass().getName());
                    return null;
                }
            }
        }

        return id;
    }
}
