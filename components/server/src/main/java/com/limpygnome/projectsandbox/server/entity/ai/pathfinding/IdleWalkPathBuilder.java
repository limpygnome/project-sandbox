package com.limpygnome.projectsandbox.server.entity.ai.pathfinding;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Created by limpygnome on 07/09/15.
 */
public interface IdleWalkPathBuilder
{

    /**
     * Constructs a randomised walkable path for the specified entity.
     *
     * This will construct a random path along either pedestrian designated tiles or random space.
     *
     * @param controller the controller
     * @param entity the entity
     * @param maxDepth the maximum steps for the entity; this excludes steps required to get to a pedestrian tile.
     * @return the path to walk
     */
    Path build(Controller controller, Entity entity, int maxDepth);

}
