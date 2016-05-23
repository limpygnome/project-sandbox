package com.projectsandbox.components.server.entity.ai.pathfinding.astar;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Major credit to the following resources:
 * - http://www.cokeandcode.com/main/tutorials/path-finding/
 */
public interface AStarHeuristic
{

    float getCost(WorldMap map, Entity entity, int tileX, int tileY, int targetTileX, int targetTileY);

}
