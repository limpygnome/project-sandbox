package com.projectsandbox.components.server.entity.ai.pathfinding.astar.heuristic;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.AStarHeuristic;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Not as accurate as an absolute heuristic, but much cheaper.
 */
public class ClosestManhattanHeuristic implements AStarHeuristic
{
    @Override
    public float getCost(WorldMap map, Entity entity, int tileX, int tileY, int targetTileX, int targetTileY)
    {
        float dx = Math.abs(targetTileX - tileX);
        float dy = Math.abs(targetTileY - tileY);

        return dx+dy;
    }
}
