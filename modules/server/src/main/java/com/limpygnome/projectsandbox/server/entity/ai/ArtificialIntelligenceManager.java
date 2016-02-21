package com.limpygnome.projectsandbox.server.entity.ai;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.idle.DefaultIdleWalkPathBuilder;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.PathFinder;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.AStarPathFinder;
import com.limpygnome.projectsandbox.server.entity.ai.pathfinding.astar.heuristic.ClosestAbsoluteHeuristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 15/07/15.
 */
@Service
public class ArtificialIntelligenceManager
{
    @Autowired
    private Controller controller;

    private PathFinder pathFinder;
    private IdleWalkPathBuilder idleWalkPathBuilder;

    public ArtificialIntelligenceManager()
    {
        // TODO: consider testing manhattan against absolute heuristic for performance
        this.pathFinder = new AStarPathFinder(new ClosestAbsoluteHeuristic());
        this.idleWalkPathBuilder = new DefaultIdleWalkPathBuilder();
    }

    public Path findPath(Entity entity, Entity target)
    {
        return findPath(entity, target.positionNew);
    }

    public Path findPath(Entity entity, Vector2 target)
    {
        return pathFinder.findPath(
                controller.mapService.mainMap,
                entity,
                entity.positionNew.x, entity.positionNew.y,
                target.x, target.y
        );
    }

    public Path findIdlePath(Entity entity, int maxDepth)
    {
        return idleWalkPathBuilder.build(controller, controller.mapService.mainMap, entity, maxDepth);
    }

}
