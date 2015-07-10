package com.limpygnome.projectsandbox.server.ents.respawn;

import com.limpygnome.projectsandbox.server.ents.Entity;

/**
 * Holds the required data for spawning an entity.
 */
public class PendingRespawn
{
    public final Entity entity;
    public final long gameTimeRespawn;

    public PendingRespawn(Entity entity)
    {
        this(entity, 0);
    }

    public PendingRespawn(Entity entity, long gameTimeRespawn)
    {
        this.entity = entity;
        this.gameTimeRespawn = gameTimeRespawn;
    }

}
