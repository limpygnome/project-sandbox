package com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.heuristic;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.AStarHeuristic;
import com.limpygnome.projectsandbox.server.world.map.Map;

/**
 * Not as accurate as an absolute heuristic, but much cheaper.
 */
public class ClosestManhattanHeuristic implements AStarHeuristic
{
    @Override
    public float getCost(Map map, Entity entity, int tileX, int tileY, int targetTileX, int targetTileY)
    {
        float dx = Math.abs(targetTileX - tileX);
        float dy = Math.abs(targetTileY - tileY);

        return dx+dy;
    }
}
