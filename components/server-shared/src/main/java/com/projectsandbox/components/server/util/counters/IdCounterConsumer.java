package com.projectsandbox.components.server.util.counters;

import java.io.Serializable;

/**
 * Implemented by a parent class, where child instances must have unique identifiers.
 */
public interface IdCounterConsumer extends Serializable
{
    /**
     * Used to indicate if the consumer, of counter identifiers, contains an identifier within its collection(s).
     *
     * @param id The ID to check
     * @return True = already exists, false = does not exist
     */
    boolean containsId(short id);
}
