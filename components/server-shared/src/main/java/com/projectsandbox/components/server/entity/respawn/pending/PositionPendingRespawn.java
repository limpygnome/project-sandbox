package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

/**
 * Respawns an entity at an exact spawn.
 */
public class PositionPendingRespawn extends PendingRespawn
{
    private final Spawn spawn;

    public PositionPendingRespawn(Controller controller, WorldMap map, Entity entity, Spawn spawn, long respawnDelay)
    {
        super(controller, map, entity, respawnDelay, false);

        this.spawn = spawn;
    }

    public PositionPendingRespawn(Controller controller, WorldMap map, Entity entity, Spawn spawn)
    {
        this(controller, map, entity, spawn, 0);
    }

    public PositionPendingRespawn(Controller controller, WorldMap map, Entity entity, float x, float y, float rotation, long respawnDelay)
    {
        super(controller, map, entity, respawnDelay, false);

        this.spawn = new Spawn(x, y, rotation);
    }

    public PositionPendingRespawn(Controller controller, WorldMap map, Entity entity, float x, float y, float rotation)
    {
        this(controller, map, entity, x, y, rotation, 0);
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        return spawn;
    }

}
