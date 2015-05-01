package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.InventoryItem;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.players.enums.PlayerKeys;

/**
 * An entity which represents a player.
 * 
 * @author limpygnome
 */
@EntityType(typeId = 1)
public class Player extends Entity
{
    public PlayerInfo playerInfo;
    public Inventory inventory;
    
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
        
        setMaxHealth(PlayerConstants.DEFAULT_HEALTH);
        
        this.playerInfo = playerInfo;
        this.inventory = new Inventory(this);
        
        // Give player default inventory items
        this.inventory.add(PlayerConstants.DEFAULT_INVENTORY_ITEMS);
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
    public void reset()
    {
    }

    @Override
    public Inventory retrieveInventory(PlayerInfo playerInfo)
    {
        if (this.playerInfo == playerInfo)
        {
            return inventory;
        }
        else
        {
            throw new RuntimeException("Attempted to retrieveInventory inventory for different player");
        }
    }
}
