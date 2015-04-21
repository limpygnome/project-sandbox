package com.limpygnome.projectsandbox.ents.vehicles;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Player;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import com.limpygnome.projectsandbox.players.PlayerInfo;
import com.limpygnome.projectsandbox.players.enums.PlayerKeys;

/**
 *
 * @author limpygnome
 */
public abstract class AbstractCar extends Entity
{   
    /**
     * The minimum (absolute) speed supported, until the speed is set to 0.
     * 
     * This avoids computation for very small movements.
     */
    public static final float SPEED_FP_MIN = 0.01f;
    
    /**
     * The space between a vehicle and an ejected player.
     */
    public static final float EJECT_SPACING = 2.0f;
    
    public static final float DEFAULT_HEALTH = 200.0f;

    // Car properties
    protected float accelerationFactor;
    protected float deaccelerationMultiplier;
    protected float steeringAngle;
    protected float maxSpeed;
    
    // Car state
    protected float speed;
    
    // Player state
    protected Vector2[] playerEjectPositions;
    protected PlayerInfo[] players;
    
    public AbstractCar(short width, short height, Vector2[] playerEjectPositions)
    {
        super(width, height);
        
        if (playerEjectPositions.length < 1)
        {
            throw new IllegalArgumentException("Must be at least one eject position for driver");
        }
        
        this.speed = 0.0f;
        this.playerEjectPositions = playerEjectPositions;
        this.players = new PlayerInfo[playerEjectPositions.length];
        
        setMaxHealth(DEFAULT_HEALTH);
    }

    @Override
    public strictfp void logic(Controller controller)
    {
        PlayerInfo playerInfoDriver = players[0];
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
                // TODO: seperate variable for reverse
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
            float wheelBase = height / 2.0f;

            Vector2 heading = new Vector2(wheelBase * (float) Math.sin(rotation), wheelBase * (float) Math.cos(rotation));
            Vector2 frontWheel = Vector2.add(position, heading);

            Vector2 backWheel = Vector2.subtract(position, heading);

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
        
        // Check if players want to get out / are still connected
        PlayerInfo playerInfo;
        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];
            
            if (playerInfo != null)
            {
                if (!playerInfo.isConnected())
                {
                    // Free the seat...
                    players[i] = null;
                }
                else if (playerInfo.isKeyDown(PlayerKeys.Action))
                {
                    // Set action key to handled
                    playerInfo.setKey(PlayerKeys.Action, false);

                    // Eject them from the vehicle
                    playerEject(controller, playerInfo, playerEjectPositions[i]);

                    // Free-up the space
                    players[i] = null;
                }
            }
        }
    }
    
    private void playerEject(Controller controller, PlayerInfo playerInfo, Vector2 ejectPosition)
    {
        // Offset position so that the playr exits to the left of the vehicle
        Vector2 plyPos = ejectPosition.clone();

        // Create new player ent in position of vehicle
        Player ply = controller.playerManager.createSetNewPlayerEnt(playerInfo);

        // Add player to pos offset
        float plyx = playerEjectVectorPos(ejectPosition.x, ply.width / 2.0f);
        float plyy = playerEjectVectorPos(ejectPosition.y, ply.height / 2.0f);
        plyPos = Vector2.add(plyPos, plyx, plyy);

        // Rotate pos to align with vehicle
        plyPos.rotate(0.0f, 0.0f, rotation);

        // Add pos of vehicle to pos
        plyPos = Vector2.add(plyPos, positionNew);

        // Set player's rotation and position
        ply.rotation(rotation);
        ply.position(plyPos);
    }
    
    private float playerEjectVectorPos(float coord, float value)
    {
        if (coord == 0)
        {
            return 0.0f;
        }
        else if (coord < 0)
        {
            return (value * -1.0f) + EJECT_SPACING;
        }
        else
        {
            return value + EJECT_SPACING;
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
            
            if (playerInfo.isKeyDown(PlayerKeys.Action))
            {
                // Set action key off/handled
                playerInfo.setKey(PlayerKeys.Action, false);
                
                // Check for next available seat
                PlayerInfo plyInSeat;
                for (int i = 0; i < players.length; i++)
                {
                    plyInSeat = players[i];
                    
                    if (plyInSeat == null || !plyInSeat.isConnected())
                    {
                        // Set the player to use this entity
                        controller.playerManager.setPlayerEnt(playerInfo, this);
                        
                        // Add as passenger
                        players[i] = playerInfo;
                        
                        break;
                    }
                }
            }
        }
        
        super.eventCollision(controller, entCollider, entVictim, entOther, result);
    }

    @Override
    public strictfp void eventDeath(Controller controller)
    {
        // Respawn players in vehicle
        PlayerInfo playerInfo;
        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];
            
            if (playerInfo != null)
            {
                controller.playerManager.createSpawnNewPlayerEnt(playerInfo);
                players[i] = null;
            }
        }
        
        super.eventDeath(controller);
    }

    @Override
    public void reset()
    {
        this.speed = 0.0f;
    }
}
