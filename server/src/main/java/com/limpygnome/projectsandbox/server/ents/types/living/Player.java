package com.limpygnome.projectsandbox.server.ents.types.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.players.enums.PlayerKeys;

import static com.limpygnome.projectsandbox.server.constants.PlayerConstants.*;

/**
 * An entity which represents a player.
 * 
 * @author limpygnome
 */
@EntityType(typeId = 1, typeName = "living/player")
public class Player extends Entity
{
    public static final String PLAYERDATA_INVENTORY_KEY = "player_inventory";

    /**
     * The distance the player travels each logic cycle.
     */
    public float movementSpeedFactor;

    /**
     * The radians the player can rotate each logic cycle.
     */
    public float rotationFactor;

    public PlayerInfo playerInfo;
    public Inventory inventory;

    public Player()
    {
        super(
                (short) 16,
                (short) 9
        );

        // Set default values
        this.movementSpeedFactor = DEFAULT_MOVEMENT_SPEED_FACTOR;
        this.rotationFactor = DEFAULT_ROTATION_FACTOR;
    }
    
    public Player(Controller controller, PlayerInfo playerInfo)
    {
        this();
        
        // Check player info is valid
        if (playerInfo == null)
        {
            throw new IllegalArgumentException("A player must have associated PlayerInfo instance!");
        }
        
        setMaxHealth(DEFAULT_HEALTH);
        
        this.playerInfo = playerInfo;

        // Load inventory
        this.inventory = (Inventory) playerInfo.session.playerData.get(PLAYERDATA_INVENTORY_KEY);

        if (this.inventory == null)
        {
            // Create new inventory
            this.inventory = new Inventory(this);

            // Give player default inventory items
            this.inventory.add(DEFAULT_INVENTORY_ITEMS);

            // Setup owner
            this.inventory.setOwner(playerInfo);
        }
    }

    @Override
    public void logic(Controller controller)
    {
        float changeDist = 0.0f;
        float changeRotation = 0.0f;
        
        // Update player's position
        if (playerInfo.isKeyDown(PlayerKeys.MovementUp))
        {
            changeDist += movementSpeedFactor;
        }
        if (playerInfo.isKeyDown(PlayerKeys.MovementDown))
        {
            changeDist -= movementSpeedFactor;
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

        // Run logic for inventory
        inventory.logic(controller);

        // Perform ent logic
        super.logic(controller);
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

    @Override
    public void eventPendingDeleted(Controller controller)
    {
        inventory.setOwner(null);
    }

    @Override
    public String friendlyName()
    {
        return playerInfo.session.displayName;
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[] { playerInfo };
    }

    @Override
    public synchronized void eventSpawn(Controller controller)
    {
        // Set player to use this entity
        controller.playerManager.setPlayerEnt(playerInfo, this);

        super.eventSpawn(controller);
    }
}
