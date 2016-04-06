package com.limpygnome.projectsandbox.game.entity.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.PlayerKeys;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

import static com.limpygnome.projectsandbox.server.constant.PlayerConstants.*;

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
        super(map, PLAYER_WIDTH, PLAYER_HEIGHT, players, new Inventory[players.length]);

        // Set default values
        this.movementSpeedFactor = DEFAULT_MOVEMENT_SPEED_FACTOR;
        this.rotationFactor = DEFAULT_ROTATION_FACTOR;

        setMaxHealth(DEFAULT_HEALTH);

        PlayerInfo playerInfo = getPlayer();

        if (playerInfo != null)
        {
            // Load inventory
            Inventory inventory = (Inventory) playerInfo.session.gameDataGet(PLAYERDATA_INVENTORY_KEY);

            if (inventory == null)
            {
                // Create new inventory with default items
                inventory = new Inventory(this);
                inventory.add(DEFAULT_INVENTORY_ITEMS);
            }

            setInventory(0, inventory);
        }
    }
    
    public Player(WorldMap map)
    {
        this(map, null);
    }

    @Override
    public synchronized void logic(Controller controller)
    {
        // Perform parent logic
        super.logic(controller);

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
                //changeX += movementFactor;
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

}
