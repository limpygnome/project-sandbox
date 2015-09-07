package com.limpygnome.projectsandbox.server.entity.ai.pathfinding;

import com.limpygnome.projectsandbox.server.entity.Entity;

/**
 * Created by limpygnome on 07/09/15.
 */
public interface IdleWalkPathBuilder
{

    /**
     * Constructs a randomised walkable path for the specified entity.
     *
     * This will construct a random path along pedestrian designated tiles.
     *
     * @param entity The entity
     * @param maxDepth The maximum steps for the entity; this excludes steps required to get to a pedestrian tile.
     * @return The path to walk
     */
    Path build(Entity entity, int maxDepth);

}
