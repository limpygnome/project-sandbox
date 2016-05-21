package com.limpygnome.projectsandbox.server.util.counters;

/**
 * Implemented by a parent class, where child instances must have unique identifiers.
 */
public interface IdCounterConsumer
{
    /**
     * Used to indicate if the consumer, of counter identifiers, contains an identifier within its collection(s).
     *
     * @param id The ID to check
     * @return True = already exists, false = does not exist
     */
    boolean containsId(short id);
}
