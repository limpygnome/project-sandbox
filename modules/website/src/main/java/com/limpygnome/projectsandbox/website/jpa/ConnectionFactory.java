package com.limpygnome.projectsandbox.website.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

/**
 * Created by limpygnome on 26/04/15.
 */
public class ConnectionFactory
{
    private static ConnectionFactory instance = null;

    private HashMap<String, EntityManagerFactory> factories;

    public ConnectionFactory()
    {
        this.factories = new HashMap<>();

        // Load all of the persistence unit factories
        for (ConnectionType type : ConnectionType.values())
        {
            this.factories.put(type.PERSISTENCE_UNIT, Persistence.createEntityManagerFactory(type.PERSISTENCE_UNIT));
        }
    }

    public EntityManager create(ConnectionType type)
    {
        return factories.get(type.PERSISTENCE_UNIT).createEntityManager();
    }

    public static synchronized ConnectionFactory getInstance()
    {
        if (instance == null)
        {
            instance = new ConnectionFactory();
        }
        return instance;
    }
}
