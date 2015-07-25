package com.limpygnome.projectsandbox.server.ents.respawn.pending;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.respawn.PendingRespawn;
import com.limpygnome.projectsandbox.server.world.Spawn;

/**
 * Used to respawn an entity at its current set position and rotation.
 */
public class CurrentPositionRespawn extends PendingRespawn
{
    private Spawn spawn;

    public CurrentPositionRespawn(Controller controller, Entity entity)
    {
        this(controller, entity, 0);
    }

    public CurrentPositionRespawn(Controller controller, Entity entity, long respawnDelay)
    {
        super(controller, entity, respawnDelay);

        this.spawn = new Spawn(entity.positionNew.x, entity.positionNew.y, entity.rotation);
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        return spawn;
    }
}
