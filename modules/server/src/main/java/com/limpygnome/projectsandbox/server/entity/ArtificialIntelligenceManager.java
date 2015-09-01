package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.PathFinder;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.astar.AStarPathFinder;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.astar.heuristic.ClosestAbsoluteHeuristic;

/**
 * Created by limpygnome on 15/07/15.
 */
public class ArtificialIntelligenceManager
{
    private Controller controller;

    private PathFinder pathFinder;

    public ArtificialIntelligenceManager(Controller controller)
    {
        this.controller = controller;

        this.pathFinder = new AStarPathFinder(new ClosestAbsoluteHeuristic());
    }

    public Path findPath(Entity entity, Entity target)
    {
        return pathFinder.findPath(
                controller.mapManager.main,
                entity,
                entity.positionNew.x, entity.positionNew.y,
                target.positionNew.x, target.positionNew.y
        );
    }

    private void rebuildRoutesNetwork()
    {
        // Construct tree of walkable routes
        // TODO: consider existing techniques
    }
}
