package com.projectsandbox.components.server.player.network;

import com.projectsandbox.components.server.entity.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to store network data for a player.
 *
 * At present this stores a snapshot of entities within the player's scene, so we can efficiently tell them what has
 * been created/deleted.
 */
public class Scene
{
    private Set<Entity> entitiesInScene;

    public Scene()
    {
        entitiesInScene = Collections.emptySet();
    }

    public synchronized SceneUpdates update(Set<Entity> entitiesInScene)
    {
        // Create set of entities deleted
        Set<Entity> entitiesDeleted = new HashSet<>(this.entitiesInScene);
        entitiesDeleted.removeAll(entitiesInScene);

        // Create set of entities created
        Set<Entity> entitiesCreated = new HashSet<>(entitiesInScene);
        entitiesCreated.removeAll(this.entitiesInScene);

        // Update our scene
        this.entitiesInScene = new HashSet<>(entitiesInScene);

        // Create result
        SceneUpdates sceneUpdates = new SceneUpdates(entitiesCreated, entitiesDeleted);
        return sceneUpdates;
    }

}
