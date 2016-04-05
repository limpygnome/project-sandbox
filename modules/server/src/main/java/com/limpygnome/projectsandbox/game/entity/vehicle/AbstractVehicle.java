package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.entity.death.CarDamage;
import com.limpygnome.projectsandbox.server.entity.death.CarKiller;
import com.limpygnome.projectsandbox.game.entity.living.pedestrian.AbstractPedestrian;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.PositionPendingRespawn;
import com.limpygnome.projectsandbox.game.entity.living.Player;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.PlayerKeys;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

import static com.limpygnome.projectsandbox.server.constant.entity.AbstractVehicleConstants.*;

/**
 * Generic vehicle for players.
 */
public abstract class AbstractVehicle extends PlayerEntity
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
    
    // Car slotState
    public float speed;
    
    // Player slotState
    protected Vector2[] playerEjectPositions;

    // Indicates that the driver, player zero, was spawned in this vehcle - thus respawn vehicle with player on death
    protected boolean flagDriverSpawned;


    public AbstractVehicle(WorldMap map, short width, short height, PlayerInfo[] players, Inventory[] inventories, Vector2[] playerEjectPositions)
    {
        super(map, width, height, players, inventories);

        if (players == null || players.length == 0)
        {
            throw new IllegalArgumentException("Players must be defined, even if null sized array; defines number of players able to use vehicle");
        }
        else if (playerEjectPositions == null || playerEjectPositions.length == 0 || playerEjectPositions[0] == null)
        {
            throw new IllegalArgumentException("Player ejection positions must have at least one non-null item");
        }
        else if (playerEjectPositions.length < 1)
        {
            throw new IllegalArgumentException("Must be at least one eject position");
        }

        this.speed = 0.0f;
        this.playerEjectPositions = playerEjectPositions;

        this.flagDriverSpawned = (players[0] != null);
        
        setMaxHealth(DEFAULT_HEALTH);
    }

    @Override
    public synchronized strictfp void logic(Controller controller)
    {
        PlayerInfo playerInfoDriver = getPlayer();
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
                // TODO: separate variable for reverse
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
        PlayerInfo[] players = getPlayers();
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

                    // Fetch ejection seat
                    Vector2 ejectPosition;

                    if (playerEjectPositions.length >= i)
                    {
                        ejectPosition = playerEjectPositions[0];
                    }
                    else
                    {
                        ejectPosition = playerEjectPositions[i];
                    }

                    // Eject player from the vehicle
                    playerEject(controller, playerInfo, ejectPosition);

                    // Free-up the space
                    players[i] = null;

                    // Reset spawn flag if driver
                    if (playerInfo == playerInfoDriver)
                    {
                        flagDriverSpawned = false;
                    }

                    // Invoke event hook
                    eventPlayerExit(playerInfo, i);
                }
            }
        }
    }
    
    private synchronized void playerEject(Controller controller, PlayerInfo playerInfo, Vector2 ejectPosition)
    {
        // Offset position so that the player exits to the left of the vehicle
        Vector2 plyPos = ejectPosition.clone();

        // Create new player ent in position of vehicle
        Entity entityPlayer = controller.playerService.playerEntCreate(map, playerInfo);

        // Add player to pos offset
        float plyx = playerEjectVectorPos(ejectPosition.x, entityPlayer.width / 2.0f);
        float plyy = playerEjectVectorPos(ejectPosition.y, entityPlayer.height / 2.0f);
        plyPos = Vector2.add(plyPos, plyx, plyy);

        // Rotate pos to align with vehicle
        plyPos.rotate(0.0f, 0.0f, rotation);

        // Add pos of vehicle to pos
        plyPos = Vector2.add(plyPos, positionNew);

        // Spawn player
        map.respawnManager.respawn(new PositionPendingRespawn(
                controller,
                entityPlayer,
                new Spawn(plyPos.x, plyPos.y, rotation)
        ));
    }
    
    private synchronized float playerEjectVectorPos(float coord, float value)
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
    public synchronized strictfp void eventHandleCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        /*
            OLD IDEA:
            Check speed of two cars. If above threshold, calculate from threshold to speed as percent. Use percent
            against max health, e.g. threshold is 10, max speed is 100, current speed is 20...so % is (20-10)/100 = 0.1
            say max health is 100...100*0.1 = 10...

            thus we cause 10 damage to current vehicle...

            idea is that if player's race around and hit something full speed, they instantly blowup/die
         */

        // Check if player
        if (entOther instanceof PlayerEntity && !(entOther instanceof AbstractVehicle))
        {
            // Check if they're holding down action key to get in vehicle
            PlayerEntity ply = (Player) entOther;
            PlayerInfo playerInfo = ply.getPlayer();
            
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
                        // Set the player to use this (vehicle) entity
                        controller.playerService.setPlayerEnt(playerInfo, this);
                        
                        // Add as passenger
                        players[i] = playerInfo;

                        // Invoke event hook
                        eventPlayerEnter(playerInfo, i);
                        
                        break;
                    }
                }
            }
        }

        // Compute collision speed
        float speedUs = Math.abs(this.getSpeed());
        float speedOther = Math.abs(entOther.getSpeed());

        Vector2 velocityUs = Vector2.vectorFromAngle(this.rotation, speedUs);
        Vector2 velocityOther = Vector2.vectorFromAngle(entOther.rotation, speedOther);

        float collisionVelocityX = Math.abs(velocityUs.x - velocityOther.x);
        float collisionVelocityY = Math.abs(velocityUs.y - velocityOther.y);
        float collisionSpeed = Vector2.length(new Vector2(collisionVelocityX, collisionVelocityY));

        if (collisionSpeed > MINIMUM_SPEED_DAMAGE)
        {
            if (entOther instanceof Player || entOther instanceof AbstractPedestrian || entOther instanceof AbstractVehicle)
            {
                float damage = (collisionSpeed - MINIMUM_SPEED_DAMAGE) * ENT_COLLISION_SPEED_DAMAGE_MULTIPLIER;
                float equalDamage = (ENT_EQUAL_DAMAGE_RATIO * damage) / 2.0f;
                float remainingDamage = (1.0f - ENT_EQUAL_DAMAGE_RATIO) * damage;

                // Apply equal damage
                this.damage(controller, this, equalDamage, CarDamage.class);
                entOther.damage(controller, this, equalDamage, CarKiller.class);

                // Apply rest of damage to fastest
                if (speedUs > speedOther)
                {
                    entOther.damage(controller, this, remainingDamage, CarKiller.class);
                }
                else
                {
                    this.damage(controller, this, remainingDamage, CarDamage.class);
                }
            }
        }

        super.eventHandleCollision(controller, entCollider, entVictim, entOther, result);
    }

    @Override
    public synchronized void eventHandleCollisionMap(Controller controller, CollisionResultMap collisionResultMap)
    {
        // Apply damage based on speed - similar to ent collisions
        if (speed > MINIMUM_SPEED_DAMAGE)
        {
            float damage = (speed - MINIMUM_SPEED_DAMAGE) * MAP_COLLISION_SPEED_DAMAGE_MULTIPLIER;
            this.damage(controller, this, damage, CarDamage.class);
        }

        // Slow down vehicle
        speed *= MAP_COLLISION_SPEED_MULTIPLIER;

        super.eventHandleCollisionMap(controller, collisionResultMap);
    }

    @Override
    public synchronized strictfp void eventHandleDeath(Controller controller, AbstractKiller killer)
    {
        // Respawn players in vehicle
        PlayerInfo playerInfo;
        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];
            
            if (playerInfo != null && !(flagDriverSpawned && i == 0))
            {
                // Create and respawn player
                Entity entityPlayer = controller.playerService.playerEntCreate(map, playerInfo);
                map.respawnManager.respawn(new EntityPendingRespawn(controller, entityPlayer));

                // Set seat to empty
                players[i] = null;
            }
        }
        
        super.eventHandleDeath(controller, killer);
    }

    @Override
    public synchronized void eventReset(Controller controller, Spawn spawn)
    {
        super.eventReset(controller, spawn);

        this.speed = 0.0f;
    }

    @Override
    public synchronized String friendlyName()
    {
        PlayerInfo driver = players[0];

        if (driver != null)
        {
            return driver.session.getNickname();
        }
        else
        {
            return friendlyNameVehicle();
        }
    }

    public abstract String friendlyNameVehicle();

    @Override
    public synchronized float getSpeed()
    {
        return speed;
    }

    public void eventPlayerEnter(PlayerInfo playerInfo, int seat)
    {
        // Nothing by default...
    }

    public void eventPlayerExit(PlayerInfo playerInfo, int seat)
    {
        // Nothing by default...
    }

}
