package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import com.limpygnome.projectsandbox.players.PlayerInfo;
import com.limpygnome.projectsandbox.utils.CustomMath;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractCar extends Entity
{
    // The player driving the car; null if no one.
    protected PlayerInfo playerInfo;
    
    protected float accelerationFactor;
    protected float deaccelerationMultiplier;
    protected float speed;
    
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
        final float steerAngleAbs = 0.5f;//CustomMath.deg2rad(35.0f);
        
        float deaccelerationMultiplier = 0.9f;
        
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
                acceleration += -accelerationFactor;
            }
            
            // Check for steer angle
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementLeft))
            {
                steerAngle -= steerAngleAbs;
            }
            
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementRight))
            {
                steerAngle += steerAngleAbs;
            }
            
            // Compute wheel positions
            //Vector2 wheelBase = Vector2.vectorFromAngle(rotation, height / 2.0f);
            float wheelBase = height / 2.0f;
            
            Vector2 heading = new Vector2(wheelBase * (float) Math.sin(rotation), wheelBase * (float) Math.cos(rotation));
//            Vector2 steering = new Vector2((float) Math.cos(rotation+steerAngle), (float) Math.sin(rotation+steerAngle));
            
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
            
            Vector2 backWheelAccel = new Vector2(speed * (float) Math.sin(rotation), speed * (float) Math.cos(rotation)); //Vector2.multiply(heading, speed);
            Vector2 frontWheelAccel = new Vector2(speed * (float) Math.sin(rotation + steerAngle), speed * (float) Math.cos(rotation + steerAngle)); //Vector2.multiply(steering, speed);
            
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
        }
        
        
            
//            float rotationOffset = 0.0f;
//
//            // Check if to apply rotation
//            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementLeft))
//            {
//                rotationOffset -= 0.02f;
//            }
//            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementRight))
//            {
//                rotationOffset += 0.02f;
//            }
//            
//            // Check if to invert rotation
//            // TODO: replace with magnitude check
//            if (acceleration < 0.0f)
//            {
//                rotationOffset *= -1.0f;
//            }
//            
//            // Apply acceleration
//            if (acceleration != 0.0f)
//            {
//                Vector2 vAcceleration = Vector2.vectorFromAngle(rotation, acceleration);
//                velocity = Vector2.add(velocity, vAcceleration);
//            }
//            
//            // Apply rotation if velocity > 0
//            // TODO: replace with magnitude check
//            if (rotationOffset != 0.0f && (velocity.x != 0.0f || velocity.y != 0.0f))
//            {
//                rotationOffset(rotationOffset);
//            }
//        }
//        
//        // Apply deacceleration
//        if (acceleration == 0.0f)
//        {
//            velocity = Vector2.multiply(velocity, deaccelerationMultiplier);
//        }
//        // Apply velocity
//        positionOffset(velocity);
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
                // Set the player to use this entity
                controller.playerManager.setPlayerEnt(playerInfo, this);
                
                // Set this vehicle to use player
                this.playerInfo = playerInfo;
            }
        }
        
        super.eventCollision(controller, entCollider, entVictim, entOther, result);
    }
}
