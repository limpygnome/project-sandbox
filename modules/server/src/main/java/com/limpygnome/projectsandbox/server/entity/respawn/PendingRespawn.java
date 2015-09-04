package com.limpygnome.projectsandbox.server.entity.respawn;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.Spawn;

/**
 * Holds the required data for spawning an entity.
 */
public abstract class PendingRespawn
{
    public final Entity entity;
    public final long gameTimeRespawn;

    protected PendingRespawn(Controller controller, Entity entity)
    {
        this(controller, entity, 0);
    }

    protected PendingRespawn(Controller controller, Entity entity, long respawnDelay)
    {
        this.entity = entity;
        long gameTime = controller.gameTime();
        this.gameTimeRespawn = gameTime + respawnDelay;
    }

    /**
     * THe implementation returns the next spawn position. If no spawn position is available, null should be
     * returned.
     *
     * @return Spawn, or null.
     */
    public abstract Spawn getSpawnPosition(Controller controller);

}
