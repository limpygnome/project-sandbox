package com.limpygnome.projectsandbox.game.entity.vehicle;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerEjectionComponent;
import com.limpygnome.projectsandbox.server.entity.component.imp.PlayerVehicleMovementComponent;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.entity.death.CarDamage;
import com.limpygnome.projectsandbox.server.entity.death.CarKiller;
import com.limpygnome.projectsandbox.game.entity.living.pedestrian.AbstractPedestrian;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
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

    
    public static final float DEFAULT_HEALTH = 200.0f;


    
    // Player slotState

    public AbstractVehicle(WorldMap map, short width, short height, PlayerInfo[] players, Inventory[] inventories, Vector2[] playerEjectPositions)
    {
        super(map, width, height, players, inventories);

        if (players == null || players.length == 0)
        {
            throw new IllegalArgumentException("Players must be defined, even if null sized array; defines number of players able to use vehicle");
        }

        components.register(new PlayerEjectionComponent(this, playerEjectPositions));

        setMaxHealth(DEFAULT_HEALTH);
    }

    @Override
    public synchronized strictfp void eventLogic(Controller controller)
    {
        super.eventLogic(controller);


    }
    
    @Override
    public synchronized strictfp void eventCollisionEntity(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        /*
            OLD IDEA:
            Check speed of two cars. If above threshold, calculate from threshold to speed as percent. Use percent
            against max health, e.g. threshold is 10, max speed is 100, current speed is 20...so % is (20-10)/100 = 0.1
            say max health is 100...100*0.1 = 10...

            thus we cause 10 damage to current vehicle...

            idea is that if player's race around and hit something full speed, they instantly blowup/die
         */

        // Compute collision speed
//        float speedUs = Math.abs(speed);
//        float speedOther = Math.abs(entOther.getSpeed());
//
//        Vector2 velocityUs = Vector2.vectorFromAngle(this.rotation, speedUs);
//        Vector2 velocityOther = Vector2.vectorFromAngle(entOther.rotation, speedOther);
//
//        float collisionVelocityX = Math.abs(velocityUs.x - velocityOther.x);
//        float collisionVelocityY = Math.abs(velocityUs.y - velocityOther.y);
//        float collisionSpeed = Vector2.length(new Vector2(collisionVelocityX, collisionVelocityY));
//
//        if (collisionSpeed > MINIMUM_SPEED_DAMAGE)
//        {
//            if (entOther instanceof Player || entOther instanceof AbstractPedestrian || entOther instanceof AbstractVehicle)
//            {
//                float damage = (collisionSpeed - MINIMUM_SPEED_DAMAGE) * ENT_COLLISION_SPEED_DAMAGE_MULTIPLIER;
//                float equalDamage = (ENT_EQUAL_DAMAGE_RATIO * damage) / 2.0f;
//                float remainingDamage = (1.0f - ENT_EQUAL_DAMAGE_RATIO) * damage;
//
//                // Apply equal damage
//                this.damage(controller, this, equalDamage, CarDamage.class);
//                entOther.damage(controller, this, equalDamage, CarKiller.class);
//
//                // Apply rest of damage to fastest
//                if (speedUs > speedOther)
//                {
//                    entOther.damage(controller, this, remainingDamage, CarKiller.class);
//                }
//                else
//                {
//                    this.damage(controller, this, remainingDamage, CarDamage.class);
//                }
//            }
//        }

        super.eventCollisionEntity(controller, entCollider, entVictim, entOther, result);
    }

    @Override
    public synchronized void eventCollisionMap(Controller controller, CollisionResultMap collisionResultMap)
    {
//        // Apply damage based on speed - similar to ent collisions
//        if (speed > MINIMUM_SPEED_DAMAGE)
//        {
//            float damage = (speed - MINIMUM_SPEED_DAMAGE) * MAP_COLLISION_SPEED_DAMAGE_MULTIPLIER;
//            this.damage(controller, this, damage, CarDamage.class);
//        }
//
//        // Slow down vehicle
//        speed *= MAP_COLLISION_SPEED_MULTIPLIER;

        super.eventCollisionMap(controller, collisionResultMap);
    }

    @Override
    public synchronized void eventReset(Controller controller, Spawn spawn)
    {
        super.eventReset(controller, spawn);

//        this.speed = 0.0f;
    }

    @Override
    public synchronized String friendlyName()
    {
        PlayerInfo driver = getPlayer();

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

//    @Override
//    public synchronized float getSpeed()
//    {
//        return speed;
//    }

//    public synchronized float getAccelerationFactor()
//    {
//        return accelerationFactor;
//    }

}
