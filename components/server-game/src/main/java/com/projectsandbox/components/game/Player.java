package com.projectsandbox.components.game;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.constant.PlayerConstants;

/**
 * An entity which represents a player.
 */
@EntityType(typeId = 1, typeName = "living/player")
public class Player extends PlayerEntity
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

    public Player(WorldMap map, PlayerInfo[] players)
    {
        super(map, PlayerConstants.PLAYER_WIDTH, PlayerConstants.PLAYER_HEIGHT, players, new Inventory[players.length]);

        // Set default values
        this.movementSpeedFactor = PlayerConstants.DEFAULT_MOVEMENT_SPEED_FACTOR;
        this.rotationFactor = PlayerConstants.DEFAULT_ROTATION_FACTOR;

        setMaxHealth(PlayerConstants.DEFAULT_HEALTH);
    }
    
    public Player(WorldMap map)
    {
        this(map, null);
    }

    @Override
    public synchronized void eventLogic(Controller controller)
    {
        // Perform parent logic
        super.eventLogic(controller);

        // Check for movement
        float changeDist = 0.0f;
        float changeRotation = 0.0f;

        PlayerInfo playerInfo = getPlayer();

        if (playerInfo != null)
        {
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
                changeRotation = rotationFactor;
            }
            if (playerInfo.isKeyDown(PlayerKeys.MovementLeft))
            {
                changeRotation = -rotationFactor;
            }

            // Check if to update the position
            if (changeDist != 0.0f)
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
        }
    }

    @Override
    public String entityName()
    {
        return "Pedestrian";
    }

}
