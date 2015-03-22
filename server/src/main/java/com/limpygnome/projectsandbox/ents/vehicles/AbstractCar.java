package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.players.PlayerInfo;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractCar extends Entity
{
    // The player driving the car; null if no one.
    protected PlayerInfo playerInfo;
            
    protected float accelerationFactor;
    protected float maxSpeed;
    protected float friction;
    
    public AbstractCar(short width, short height)
    {
        super(width, height);
        
        playerInfo = null;
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
            }
        }
        
        super.eventCollision(controller, entCollider, entVictim, entOther, result);
    }
}
