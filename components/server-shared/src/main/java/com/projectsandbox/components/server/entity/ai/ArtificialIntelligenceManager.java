package com.projectsandbox.components.server.entity.ai;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.ai.pathfinding.Path;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Used to build paths for entity artificial intelligence.
 */
@Service
public class ArtificialIntelligenceManager
{
    @Autowired
    private Controller controller;

    public Path findPath(Entity entity, Entity target)
    {
        return findPath(entity, target.positionNew);
    }

    public Path findPath(Entity entity, Vector2 target)
    {
        WorldMap map = entity.map;
        ArtificialIntelligenceMapData mapData = map.getArtificialIntelligenceMapData();

        if (mapData.pathFinder != null)
        {
            throw new RuntimeException("Path finding not supported - map type: " + map.getClass().getName());
        }

        return mapData.pathFinder.findPath(
                entity,
                entity.positionNew.x, entity.positionNew.y,
                target.x, target.y
        );
    }

    public Path findIdlePath(Entity entity, int maxDepth)
    {
        WorldMap map = entity.map;
        ArtificialIntelligenceMapData mapData = map.getArtificialIntelligenceMapData();

        if (mapData.idleWalkPathBuilder == null)
        {
            throw new RuntimeException("Idle path finding not supported for map type - map type: " + map.getClass().getName());
        }

        Path path = mapData.idleWalkPathBuilder.build(controller, entity, maxDepth);
        return path;
    }

}
