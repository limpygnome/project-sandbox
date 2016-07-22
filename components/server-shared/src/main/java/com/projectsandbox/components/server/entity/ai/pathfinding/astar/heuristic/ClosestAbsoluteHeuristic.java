package com.projectsandbox.components.server.entity.ai.pathfinding.astar.heuristic;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.AStarHeuristic;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Accurate but expensive heuristic.
 */
public class ClosestAbsoluteHeuristic implements AStarHeuristic
{
    private static final long serialVersionUID = 1L;

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
