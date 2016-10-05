package com.projectsandbox.components.server.entity.ai;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.projectsandbox.components.server.entity.ai.pathfinding.PathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.TileAStarPathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.heuristic.ClosestAbsoluteHeuristic;
import com.projectsandbox.components.server.entity.ai.pathfinding.idle.DefaultTileIdleWalkPathBuilder;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by limpygnome on 22/07/16.
 */
@Component
@Scope(value = "prototype")
public class ArtificialIntelligenceMapData implements MapData
{
    /* Used to find paths */
    protected PathFinder pathFinder;

    /* Used to build idle walk paths */
    protected IdleWalkPathBuilder idleWalkPathBuilder;

    public ArtificialIntelligenceMapData()
    {
    }

    @Override
    public void serialize(Controller controller, WorldMap map, JSONObject root)
    {
    }

    @Override
    public void deserialize(Controller controller, WorldMap map, JSONObject root)
    {
        // No actual data is read, but lets
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

}
