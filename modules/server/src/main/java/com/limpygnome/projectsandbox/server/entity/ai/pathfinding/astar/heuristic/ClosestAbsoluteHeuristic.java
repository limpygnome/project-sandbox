package com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.heuristic;

import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.AStarHeuristic;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Accurate but expensive heuristic.
 */
public class ClosestAbsoluteHeuristic implements AStarHeuristic
{
    @Override
    public float getCost(WorldMap map, Entity entity, int tileX, int tileY, int targetTileX, int targetTileY)
    {
        float dx = (targetTileX - tileX);
        float dy = (targetTileY - tileY);

        return (float) Math.sqrt(
                (dx * dx) + (dy * dy)
        );
    }
}
