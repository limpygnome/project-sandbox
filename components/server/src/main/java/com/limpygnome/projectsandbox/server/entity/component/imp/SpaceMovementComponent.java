package com.limpygnome.projectsandbox.server.entity.component.imp;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.component.EntityComponent;
import com.limpygnome.projectsandbox.server.entity.component.event.CollisionMapComponentEvent;
import com.limpygnome.projectsandbox.server.entity.component.event.LogicComponentEvent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.map.CollisionMapResult;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.PlayerKeys;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Created by limpygnome on 14/04/16.
 */
public class SpaceMovementComponent implements EntityComponent, LogicComponentEvent, CollisionMapComponentEvent
{
    private float speedLimit;

    public SpaceMovementComponent(float speedLimit)
    {
        this.speedLimit = speedLimit;
    }

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        PlayerInfo playerDriver = playerEntity.getPlayer();

        if (playerDriver != null)
        {
            VelocityComponent velocityComponent = (VelocityComponent) entity.components.fetchComponent(VelocityComponent.class);

            // Handle changing the rotation
            float angleOffset = 0.0f;

            if (playerDriver.isKeyDown(PlayerKeys.MovementLeft))
            {
                angleOffset -= 0.2f;
            }

            if (playerDriver.isKeyDown(PlayerKeys.MovementRight))
            {
                angleOffset += 0.2f;
            }

            entity.rotationOffset(angleOffset);

            // Handle acceleration
            Vector2 offset = null;

            if (playerDriver.isKeyDown(PlayerKeys.MovementUp))
            {
                offset = Vector2.vectorFromAngle(entity.rotation, 0.5f);
            }
            else if (playerDriver.isKeyDown(PlayerKeys.MovementDown))
            {
                offset = Vector2.vectorFromAngle(entity.rotation, -0.5f);
            }

            if (offset != null)
            {
                synchronized (velocityComponent)
                {
                    Vector2 velocity = velocityComponent.getVelocity();
                    velocity.offset(offset);

                    // Keep within speed limit
                    float currentSpeed = velocity.length();
                    if (currentSpeed > speedLimit)
                    {
                        velocity.normalise();
                        velocity.multiply(speedLimit);
                    }
                }
            }
        }

    }

    @Override
    public void eventCollisionMap(Controller controller, Entity entity, CollisionMapResult collisionMapResult)
    {
        WorldMap worldMap = entity.map;

        // Invert velocity
        VelocityComponent velocityComponent = (VelocityComponent) entity.components.fetchComponent(VelocityComponent.class);

        synchronized (velocityComponent)
        {
            Vector2 velocity = velocityComponent.getVelocity();
            velocity.perp();
        }

        // Make sure we're within map to avoid death
        entity.positionNew.limit(0.0f, worldMap.getMaxX(), 0.0f, worldMap.getMaxY());
    }

}
