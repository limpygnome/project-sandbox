package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

/**
 * Used to respawn an entity at its current set position and rotation.
 */
public class CurrentPositionRespawn extends PendingRespawn
{
    private Spawn spawn;

    public CurrentPositionRespawn(Controller controller, WorldMap map, Entity entity)
    {
        this(controller, map, entity, 0);
    }

    public CurrentPositionRespawn(Controller controller, WorldMap map, Entity entity, long respawnDelay)
    {
        super(controller, map, entity, respawnDelay, false);

        this.spawn = new Spawn(entity.positionNew.x, entity.positionNew.y, entity.rotation);
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        return spawn;
    }
}
