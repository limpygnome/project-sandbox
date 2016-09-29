package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

import java.io.Serializable;

/**
 * Holds the required data for spawning an entity.
 */
public abstract class PendingRespawn implements Serializable
{
    private static final long serialVersionUID = 1L;

    public final WorldMap map;
    public final Entity entity;
    public final long gameTimeRespawn;
    public final boolean loadedFromSession;

    protected PendingRespawn(Controller controller, WorldMap map, Entity entity, long respawnDelay, boolean loadedFromSession)
    {
        this.map = map;
        this.entity = entity;
        long gameTime = controller.gameTime();
        this.gameTimeRespawn = gameTime + respawnDelay;
        this.loadedFromSession = loadedFromSession;
    }

    /**
     * THe implementation returns the next spawn position. If no spawn position is available, null should be
     * returned.
     *
     * @return Spawn, or null.
     */
    public abstract Spawn getSpawnPosition(Controller controller);

}
