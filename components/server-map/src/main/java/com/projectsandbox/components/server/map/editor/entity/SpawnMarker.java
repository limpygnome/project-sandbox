package com.projectsandbox.components.server.map.editor.entity;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.world.spawn.Spawn;

/**
 * Used to represent a spawn when editing a map.
 *
 * The faction of the entity should be the same as the faction used for the spawn.
 */
@EntityType(typeId = 902, typeName = "util/spawn-marker")
public class SpawnMarker extends PlayerEntity
{
    private Spawn spawn;

    public SpawnMarker()
    {
        super((short) 32, (short) 32);
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        super.eventSpawn(controller, spawn);

        // Add spawn at current position
        this.spawn = spawn;

        // TODO...
    }

    @Override
    public synchronized void eventDeath(Controller controller, AbstractKiller killer)
    {
        // Remove associated spawn
    }

    @Override
    public String entityName()
    {
        return "Spawn";
    }

}
