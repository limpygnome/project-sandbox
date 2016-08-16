package com.projectsandbox.components.game;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

import java.io.Serializable;

/**
 * Created by limpygnome on 12/04/16.
 *
 * TODO: need to workout how to use this with velocity component
 */
public class PlayerVehicleMovementComponent implements Serializable, EntityComponent, LogicComponentEvent
{
    private static final long serialVersionUID = 1L;

    /**
     * The minimum (absolute) speed supported, until the speed is set to 0.
     *
     * This avoids computation for very small movements.
     */
    public static final float SPEED_FP_MIN = 0.01f;

    // Car properties
    protected float accelerationFactor;
    protected float deaccelerationMultiplier;
    protected float steeringAngle;
    protected float maxSpeed;

    // Car slotState
    protected float speed;

    public PlayerVehicleMovementComponent(float accelerationFactor, float deaccelerationMultiplier, float steeringAngle, float maxSpeed)
    {
        this.accelerationFactor = accelerationFactor;
        this.deaccelerationMultiplier = deaccelerationMultiplier;
        this.steeringAngle = steeringAngle;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        PlayerInfo playerInfoDriver = playerEntity.getPlayer();

        float acceleration = 0.0f;
        float steerAngle = 0.0f;

        // Check player keys
        if (playerInfoDriver != null)
        {
            // Check if to apply power/reverse
            if (playerInfoDriver.isKeyDown(PlayerKeys.MovementUp))
            {
                acceleration = accelerationFactor;
            }
            if (playerInfoDriver.isKeyDown(PlayerKeys.MovementDown))
            {
                // TODO: separate variable for reverse
                acceleration -= accelerationFactor;
            }

            // Check for steer angle
            if (playerInfoDriver.isKeyDown(PlayerKeys.MovementLeft))
            {
                steerAngle -= this.steeringAngle;
            }

            if (playerInfoDriver.isKeyDown(PlayerKeys.MovementRight))
            {
                steerAngle += this.steeringAngle;
            }
        }

        if (speed != 0.0f || acceleration != 0.0f)
        {
            // Compute wheel positions
            float wheelBase = entity.height / 2.0f;

            Vector2 heading = new Vector2(wheelBase * (float) Math.sin(entity.rotation), wheelBase * (float) Math.cos(entity.rotation));
            Vector2 frontWheel = entity.positionNew.clone().add(heading);
            Vector2 backWheel = entity.positionNew.clone().subtract(heading);

            // Offset wheels by acceleration
            if (acceleration != 0.0f)
            {
                this.speed += acceleration;

                // Clamp speed
                if (this.speed < -maxSpeed)
                {
                    this.speed = -maxSpeed;
                }
                else if (this.speed > maxSpeed)
                {
                    this.speed = maxSpeed;
                }
            }
            else if (deaccelerationMultiplier != 1.0f)
            {
                this.speed *= deaccelerationMultiplier;
            }

            Vector2 backWheelAccel = new Vector2(speed * (float) Math.sin(entity.rotation), speed * (float) Math.cos(entity.rotation));
            Vector2 frontWheelAccel = new Vector2(speed * (float) Math.sin(entity.rotation + steerAngle), speed * (float) Math.cos(entity.rotation + steerAngle));

            backWheel.add(backWheelAccel);
            frontWheel.add(frontWheelAccel);

            // Compute car position
            Vector2 carPosition = new Vector2(
                    (frontWheel.x + backWheel.x) / 2.0f,
                    (frontWheel.y + backWheel.y) / 2.0f
            );
            entity.position(carPosition);

            // Compute new rotation
            float newRotation = (float) Math.atan2(frontWheel.x - backWheel.x, frontWheel.y - backWheel.y);
            entity.rotation(newRotation);

            // Check if to set speed to 0
            if (Math.abs(speed) < SPEED_FP_MIN)
            {
                speed = 0.0f;
            }
        }
    }

    public float getSpeed()
    {
        return speed;
    }

}
