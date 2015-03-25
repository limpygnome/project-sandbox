package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import com.limpygnome.projectsandbox.players.PlayerInfo;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractCar extends Entity
{
    private final float SPEED_FP_MIN = 0.01f;
    
    // The player driving the car; null if no one.
    protected PlayerInfo playerInfo;
    
    protected float accelerationFactor;
    protected float deaccelerationMultiplier;
    protected float speed;
    protected float steeringAngle;
    
    public AbstractCar(short width, short height)
    {
        super(width, height);
        
        playerInfo = null;
        speed = 0.0f;
    }

    @Override
    public strictfp void logic(Controller controller)
    {
        float acceleration = 0.0f;
        float steerAngle = 0.0f;
        
        // Check player keys
        if (playerInfo != null)
        {
            // Check if to apply power/reverse
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementUp))
            {
                acceleration = accelerationFactor;
            }
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementDown))
            {
                // TODO: seperate variable for reverse
                acceleration -= accelerationFactor;
            }
            
            // Check for steer angle
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementLeft))
            {
                steerAngle -= this.steeringAngle;
            }
            
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementRight))
            {
                steerAngle += this.steeringAngle;
            }
            
            // Check if player is trying to get out
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.Action))
            {
                // Set action to up/handled
                playerInfo.setKey(PlayerInfo.PlayerKey.Action, false);
                
                // Create new player in position of vehicle
                controller.playerManager.createSetNewPlayerEnt(playerInfo, positionNew);
                
                // Reset player in car
                this.playerInfo = null;
            }
        }
        
        if (speed != 0.0f || acceleration != 0.0f)
        {
            // Compute wheel positions
            float wheelBase = height / 2.0f;

            Vector2 heading = new Vector2(wheelBase * (float) Math.sin(rotation), wheelBase * (float) Math.cos(rotation));
            Vector2 frontWheel = Vector2.add(position, heading);

            Vector2 backWheel = Vector2.subtract(position, heading);

            // Offset wheels by acceleration
            if (acceleration != 0.0f)
            {
                this.speed += acceleration;
            }
            else
            {
                this.speed *= deaccelerationMultiplier;
            }

            Vector2 backWheelAccel = new Vector2(speed * (float) Math.sin(rotation), speed * (float) Math.cos(rotation));
            Vector2 frontWheelAccel = new Vector2(speed * (float) Math.sin(rotation + steerAngle), speed * (float) Math.cos(rotation + steerAngle));

            frontWheel = Vector2.add(frontWheel, frontWheelAccel);
            backWheel = Vector2.add(backWheel, backWheelAccel);

            // Compute car position
            Vector2 carPosition = new Vector2(
                    (frontWheel.x + backWheel.x) / 2.0f,
                    (frontWheel.y + backWheel.y) / 2.0f
            );
            position(carPosition);

            // Compute new rotation
            float newRotation = (float) Math.atan2(frontWheel.x - backWheel.x, frontWheel.y - backWheel.y);
            rotation(newRotation);

            // Check if to set speed to 0
            if (Math.abs(speed) < SPEED_FP_MIN)
            {
                speed = 0.0f;
            }
        }
    }

    @Override
    public strictfp void eventCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        if (entOther instanceof Player)
        {
            // Check if they're holding down action key to get in vehicle
            Player ply = (Player) entOther;
            PlayerInfo playerInfo = ply.playerInfo;
            
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.Action))
            {
                // Set action key off/handled
                playerInfo.setKey(PlayerInfo.PlayerKey.Action, false);
                
                // Set the player to use this entity
                controller.playerManager.setPlayerEnt(playerInfo, this);
                
                // Set this vehicle to use player
                this.playerInfo = playerInfo;
            }
        }
        
        super.eventCollision(controller, entCollider, entVictim, entOther, result);
    }
}
