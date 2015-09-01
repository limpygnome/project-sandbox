package com.limpygnome.projectsandbox.server.entity.physics.pathfinding;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.Map;

/**
 * Created by limpygnome on 01/09/15.
 */
public interface PathFinder
{

    Path findPath(Map map, Entity entity, float startX, float startY, float endX, float endY);

}
