package com.projectsandbox.components.game;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.CollisionMapComponentEvent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

/**
 * Used to add space simulated movement to an entity.
 */
public class SpaceMovementComponent implements EntityComponent, LogicComponentEvent, CollisionMapComponentEvent
{
    private static final long serialVersionUID = 1L;

    private float speedLimit;
    private float acceleration;
    private float turnRadians;

    public SpaceMovementComponent(float speedLimit, float acceleration, float turnRadians)
    {
        this.speedLimit = speedLimit;
        this.acceleration = acceleration;
        this.turnRadians = turnRadians;
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

            if (playerDriver.keys.isKeyDown(PlayerKeys.MovementLeft))
            {
                angleOffset -= turnRadians;
            }

            if (playerDriver.keys.isKeyDown(PlayerKeys.MovementRight))
            {
                angleOffset += turnRadians;
            }

            entity.rotationOffset(angleOffset);

            // Handle acceleration
            Vector2 offset = null;

            if (playerDriver.keys.isKeyDown(PlayerKeys.MovementUp))
            {
                offset = Vector2.vectorFromAngle(entity.rotation, acceleration);
            }
            else if (playerDriver.keys.isKeyDown(PlayerKeys.MovementDown))
            {
                offset = Vector2.vectorFromAngle(entity.rotation, -acceleration);
            }

            if (offset != null)
            {
                synchronized (velocityComponent)
                {
                    Vector2 velocity = velocityComponent.getVelocity();
                    velocity.add(offset);

                    // Keep within speed limit
                    velocity.limit(speedLimit);
                }
            }
        }

    }

    @Override
    public void eventCollisionMap(Controller controller, Entity entity, CollisionMapResult collisionMapResult)
    {
        // Invert velocity
        VelocityComponent velocityComponent = (VelocityComponent) entity.components.fetchComponent(VelocityComponent.class);

        synchronized (velocityComponent)
        {
            Vector2 velocity = velocityComponent.getVelocity();
            velocity.perp();
        }
    }

}
