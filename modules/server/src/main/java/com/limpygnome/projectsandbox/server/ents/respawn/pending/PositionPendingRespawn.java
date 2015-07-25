package com.limpygnome.projectsandbox.server.ents.respawn.pending;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.respawn.PendingRespawn;
import com.limpygnome.projectsandbox.server.world.Spawn;

/**
 * Respawns an entity at an exact spawn.
 */
public class PositionPendingRespawn extends PendingRespawn
{
    private final Spawn spawn;

    public PositionPendingRespawn(Controller controller, Entity entity, Spawn spawn)
    {
        super(controller, entity, 0);

        this.spawn = spawn;
    }

    public PositionPendingRespawn(Controller controller, Entity entity, Spawn spawn, long respawnDelay)
    {
        super(controller, entity, respawnDelay);

        this.spawn = spawn;
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        return spawn;
    }
}
