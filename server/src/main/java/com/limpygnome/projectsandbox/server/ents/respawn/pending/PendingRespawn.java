package com.limpygnome.projectsandbox.server.ents.respawn.pending;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.world.Spawn;

/**
 * Holds the required data for spawning an entity.
 */
public abstract class PendingRespawn
{
    public final Entity entity;
    public final long gameTimeRespawn;

    protected PendingRespawn(Entity entity)
    {
        this(entity, 0);
    }

    protected PendingRespawn(Entity entity, long gameTimeRespawn)
    {
        this.entity = entity;
        this.gameTimeRespawn = gameTimeRespawn;
    }

    /**
     * THe implementation returns the next spawn position. If no spawn position is available, null should be
     * returned.
     *
     * @return Spawn, or null.
     */
    public abstract Spawn getSpawnPosition(Controller controller);

}
