package com.projectsandbox.components.server.entity.ai;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.projectsandbox.components.server.entity.ai.pathfinding.idle.DefaultTileIdleWalkPathBuilder;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.ai.pathfinding.Path;
import com.projectsandbox.components.server.entity.ai.pathfinding.PathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.TileAStarPathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.heuristic.ClosestAbsoluteHeuristic;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;

/**
 * Used to build paths for entity artificial intelligence.
 */
public class ArtificialIntelligenceManager
{
    private Controller controller;
    private WorldMap map;

    /* Used to find paths */
    private PathFinder pathFinder;

    /* Used to build idle walk paths */
    private IdleWalkPathBuilder idleWalkPathBuilder;

    public ArtificialIntelligenceManager(Controller controller, WorldMap map)
    {
        this.controller = controller;
        this.map = map;

        // Select implementations based on map
        if (map instanceof TileWorldMap)
        {
            // TODO: consider testing manhattan against absolute heuristic for performance
            this.pathFinder = new TileAStarPathFinder(new ClosestAbsoluteHeuristic());
            this.idleWalkPathBuilder = new DefaultTileIdleWalkPathBuilder();
        }
        else
        {
            this.pathFinder = null;
            this.idleWalkPathBuilder = null;
        }
    }

    public Path findPath(Entity entity, Entity target)
    {
        return findPath(entity, target.positionNew);
    }

    public Path findPath(Entity entity, Vector2 target)
    {
        if (pathFinder != null)
        {
            throw new RuntimeException("Path finding not supported - map type: " + map.getClass().getName());
        }

        return pathFinder.findPath(
                entity,
                entity.positionNew.x, entity.positionNew.y,
                target.x, target.y
        );
    }

    public Path findIdlePath(Entity entity, int maxDepth)
    {
        if (idleWalkPathBuilder == null)
        {
            throw new RuntimeException("Idle path finding not supported for map type - map type: " + map.getClass().getName());
        }

        return idleWalkPathBuilder.build(controller, entity, maxDepth);
    }

}
