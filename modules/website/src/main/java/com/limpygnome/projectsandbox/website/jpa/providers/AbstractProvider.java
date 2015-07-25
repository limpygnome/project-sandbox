package com.limpygnome.projectsandbox.website.jpa.providers;

import com.limpygnome.projectsandbox.website.jpa.ConnectionFactory;
import com.limpygnome.projectsandbox.website.jpa.ConnectionType;

import javax.persistence.EntityManager;

/**
 * Created by limpygnome on 26/04/15.
 */
public abstract class AbstractProvider
{
    protected EntityManager em;

    public AbstractProvider(EntityManager em)
    {
        this.em = em;
    }

    public AbstractProvider(ConnectionType connectionType)
    {
        this.em = ConnectionFactory.getInstance().create(connectionType);
    }

    public void begin()
    {
        em.getTransaction().begin();
    }

    public void commit()
    {
        em.getTransaction().commit();
    }

    public void rollback()
    {
        em.getTransaction().rollback();
    }

    public void close()
    {
        em.close();
    }

    public EntityManager getEntityManager()
    {
        return em;
    }
}
