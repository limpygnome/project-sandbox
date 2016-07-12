package com.projectsandbox.components.server.entity.respawn;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.spawn.Spawn;

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
