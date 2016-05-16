package com.limpygnome.projectsandbox.server.player.network;

import com.limpygnome.projectsandbox.server.entity.Entity;

import java.util.Set;

/**
 * Used to hold the result of scene updates to {@link Scene}.
 */
public class SceneUpdates
{
    public final Set<Entity> entitiesCreated;

    public final Set<Entity> entitiesDeleted;

    public SceneUpdates(Set<Entity> entitiesCreated, Set<Entity> entitiesDeleted)
    {
        this.entitiesCreated = entitiesCreated;
        this.entitiesDeleted = entitiesDeleted;
    }

}
