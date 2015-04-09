package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import com.limpygnome.projectsandbox.players.PlayerInfo;
import com.limpygnome.projectsandbox.players.enums.PlayerKeys;

/**
 * An entity which represents a player.
 * 
 * @author limpygnome
 */
@EntityType(typeId = 1)
public class Player extends Entity implements PropertyFaction
{
    public PlayerInfo playerInfo;
    
    public Player(Controller controller, PlayerInfo playerInfo)
    {
        super(
                (short) 16,
                (short) 9
        );
        
        // Check player info is valid
        if (playerInfo == null)
        {
            throw new IllegalArgumentException("A player must have associated PlayerInfo instance!");
        }
        
        this.playerInfo = playerInfo;
    }

    @Override
    public void logic(Controller controller)
    {
        final float movementFactor = 2.0f;
        final float rotationFactor = 0.25f;
        
        float changeDist = 0.0f;
        float changeRotation = 0.0f;
        
        // Update player's position
        if (playerInfo.isKeyDown(PlayerKeys.MovementUp))
        {
            changeDist += movementFactor;
        }
        if (playerInfo.isKeyDown(PlayerKeys.MovementDown))
        {
            changeDist -= movementFactor;
        }
        
        // Check for rotation
        if (playerInfo.isKeyDown(PlayerKeys.MovementRight))
        {
            //changeX += movementFactor;
            changeRotation = rotationFactor;
        }
        if (playerInfo.isKeyDown(PlayerKeys.MovementLeft))
        {
            changeRotation = -rotationFactor;
        }

        // Check if to update the position
        if(changeDist != 0.0f)
        {
            // Project dist for angle
            Vector2 rotatedDist = Vector2.vectorFromAngle(rotation, changeDist);
            positionOffset(rotatedDist);
        }
        
        // Check if to update the rotation
        if (changeRotation != 0.0f)
        {
            rotationOffset(changeRotation);
        }
        
        // Perform ent logic
        super.logic(controller);
    }

    @Override
    public Faction getPropertyFaction()
    {
        return Faction.NONE;
    }
}
