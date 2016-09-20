package com.projectsandbox.components.game;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.CollisionMapComponentEvent;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * A component to keep an entity within the map.
 */
public class MapBoundsComponent implements EntityComponent, CollisionMapComponentEvent
{

    @Override
    public void eventCollisionMap(Controller controller, Entity entity, CollisionMapResult collisionMapResult)
    {
        WorldMap worldMap = entity.map;

        // Make sure we're within map to avoid death
        entity.positionNew.limit(0.0f, worldMap.getMaxX(), 0.0f, worldMap.getMaxY());
    }

}
