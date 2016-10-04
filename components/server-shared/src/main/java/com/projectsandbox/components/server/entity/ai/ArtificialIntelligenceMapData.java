package com.projectsandbox.components.server.entity.ai;

import com.projectsandbox.components.server.entity.ai.pathfinding.IdleWalkPathBuilder;
import com.projectsandbox.components.server.entity.ai.pathfinding.PathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.TileAStarPathFinder;
import com.projectsandbox.components.server.entity.ai.pathfinding.astar.heuristic.ClosestAbsoluteHeuristic;
import com.projectsandbox.components.server.entity.ai.pathfinding.idle.DefaultTileIdleWalkPathBuilder;
import com.projectsandbox.components.server.world.map.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.tile.TileWorldMap;
import org.json.simple.JSONObject;

/**
 * Created by limpygnome on 22/07/16.
 */
public class ArtificialIntelligenceMapData implements MapData
{
    /* Used to find paths */
    protected PathFinder pathFinder;

    /* Used to build idle walk paths */
    protected IdleWalkPathBuilder idleWalkPathBuilder;

    public ArtificialIntelligenceMapData(WorldMap map)
    {
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

    @Override
    public void serialize(JSONObject root)
    {
    }

    @Override
    public void deserialize(JSONObject root)
    {
    }

}
