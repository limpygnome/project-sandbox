package com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Major credit to the following resources:
 * - http://www.cokeandcode.com/main/tutorials/path-finding/
 */
public interface AStarHeuristic
{

    float getCost(WorldMap map, Entity entity, int tileX, int tileY, int targetTileX, int targetTileY);

}
