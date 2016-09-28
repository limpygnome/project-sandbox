package com.projectsandbox.components.server.entity.respawn.pending;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.event.ProjectInFrontOfEntityComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

import java.util.Set;

/**
 * Created by limpygnome on 27/09/16.
 */
public class ProjectInFrontOfEntityRespawn extends PendingRespawn
{
    private Entity parent;
    private float spacing;
    private Vector2 offset;

    public ProjectInFrontOfEntityRespawn(Controller controller, WorldMap map, Entity entity, Entity parent, float spacing, Vector2 offset)
    {
        super(controller, map, entity, 0);

        this.parent = parent;
        this.spacing = spacing;
        this.offset = offset;
    }

    @Override
    public Spawn getSpawnPosition(Controller controller)
    {
        // Clone the rotation of the parent
        float rotation = parent.rotation;

        // Calculate new position, so we're in front of parent
        Vector2 newPosition = parent.positionNew.clone();

        newPosition.add(Vector2.vectorFromAngle(rotation, parent.height / 2.0f));
        newPosition.add(Vector2.vectorFromAngle(rotation, entity.height / 2.0f));
        newPosition.add(Vector2.vectorFromAngle(rotation, spacing));

        if (offset != null)
        {
            newPosition.add(Vector2.vectorFromAngle(rotation, offset));
        }

        // Make any callbacks to components for this type of respawn/event
        Set<ProjectInFrontOfEntityComponentEvent> callbacks = entity.components.fetch(ProjectInFrontOfEntityComponentEvent.class);

        for (ProjectInFrontOfEntityComponentEvent callback : callbacks)
        {
            callback.projectInFrontOfEntity(entity, parent, spacing, newPosition);
        }

        Spawn finalSpawn = new Spawn(newPosition.x, newPosition.y, rotation);
        return finalSpawn;
    }

}
