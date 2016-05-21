package com.limpygnome.projectsandbox.server.entity.ai.pathfinding;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Used for finding a path between two points.
 */
public interface PathFinder
{

    Path findPath(Entity entity, float startX, float startY, float endX, float endY);

}
