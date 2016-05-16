package com.limpygnome.projectsandbox.server.player.network;

import com.limpygnome.projectsandbox.server.entity.Entity;

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
        entitiesInScene = new HashSet<>();
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
