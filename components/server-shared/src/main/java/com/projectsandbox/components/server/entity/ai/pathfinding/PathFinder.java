package com.projectsandbox.components.server.entity.ai.pathfinding;

import com.projectsandbox.components.server.entity.Entity;

import java.io.Serializable;

/**
 * Used for finding a path between two points.
 */
public interface PathFinder extends Serializable
{

    Path findPath(Entity entity, float startX, float startY, float endX, float endY);

}
