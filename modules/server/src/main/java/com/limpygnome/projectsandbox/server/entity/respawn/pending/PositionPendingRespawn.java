package com.limpygnome.projectsandbox.server.entity.respawn.pending;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.respawn.PendingRespawn;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

/**
 * Respawns an entity at an exact spawn.
 */
public class PositionPendingRespawn extends PendingRespawn
{
    private final Spawn spawn;

    public PositionPendingRespawn(Controller controller, Entity entity, Spawn spawn, long respawnDelay)
    {
        super(controller, entity, respawnDelay);

        this.spawn = spawn;
    }

    public PositionPendingRespawn(Controller controller, Entity entity, Spawn spawn)
    {
        this(controller, entity, spawn, 0);
    }

    public PositionPendingRespawn(Controller controller, Entity entity, float x, float y, float rotation, long respawnDelay)
    {
        super(controller, entity, respawnDelay);

        this.spawn = new Spawn(x, y, rotation);
    }

    public PositionPendingRespawn(Controller controller, Entity entity, float x, float y, float rotation)
    {
        this(controller, entity, x, y, rotation, 0);
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        return spawn;
    }

}
