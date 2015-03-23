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
    // The player driving the car; null if no one.
    protected PlayerInfo playerInfo;
    
    protected Vector2 velocity;
    
    protected float accelerationFactor;
    protected float deaccelerationMultiplier;
    protected float maxSpeed;
    protected float friction;
    
    public AbstractCar(short width, short height)
    {
        super(width, height);
        
        playerInfo = null;
        velocity = new Vector2();
    }

    @Override
    public strictfp void logic(Controller controller)
    {
        float acceleration = 0.0f;
        
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
            
            float rotationOffset = 0.0f;

            // Check if to apply rotation
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementLeft))
            {
                rotationOffset -= 0.02f;
            }
            if (playerInfo.isKeyDown(PlayerInfo.PlayerKey.MovementRight))
            {
                rotationOffset += 0.02f;
            }
            
            // Check if to invert rotation
            // TODO: replace with magnitude check
            if (acceleration < 0.0f)
            {
                rotationOffset *= -1.0f;
            }
            
            // Apply acceleration
            if (acceleration != 0.0f)
            {
                Vector2 vAcceleration = Vector2.vectorFromAngle(rotation, acceleration);
                velocity = Vector2.add(velocity, vAcceleration);
            }
            
            // Apply rotation if velocity > 0
            // TODO: replace with magnitude check
            if (rotationOffset != 0.0f && (velocity.x != 0.0f || velocity.y != 0.0f))
            {
                rotationOffset(rotationOffset);
            }
        }
        
        // Apply deacceleration
        if (acceleration == 0.0f)
        {
            velocity = Vector2.multiply(velocity, deaccelerationMultiplier);
        }
        // Apply velocity
        positionOffset(velocity);
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
