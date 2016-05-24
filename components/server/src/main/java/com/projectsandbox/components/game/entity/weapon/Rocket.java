package com.projectsandbox.components.game.entity.weapon;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.component.imp.VelocityComponent;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.entity.death.RocketKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.entity.physics.spatial.SpatialActions;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_BLAST_DAMAGE;
import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_BLAST_RADIUS;
import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_LIFESPAN_MS;
import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_SPEED;
import static com.projectsandbox.components.server.constant.weapon.RocketConstants.ROCKET_SPEED_STEP;

/**
 * An Rocket fired by a rocket weapon.
 */
@EntityType(typeId = 600, typeName = "weapon/rocket")
public class Rocket extends Entity
{
    private final static Logger LOG = LogManager.getLogger(Rocket.class);

    private long gameTimeCreated;
    private boolean exploded;

    private PlayerInfo playerInfoOwner;

    public Rocket(WorldMap map, Controller controller, PlayerInfo playerInfoOwner, float initialSpeed)
    {
        super(map, (short) 9, (short) 12);

        this.playerInfoOwner = playerInfoOwner;
        this.gameTimeCreated = controller.gameTime();
        this.exploded = false;

        setMaxHealth(1);

        components.register(new VelocityComponent(
                100.0f      // Mass
        ));
    }

    /**
     *
     * @param controller
     * @param playerInfo Can be null
     */
    public Rocket(WorldMap map, Controller controller, PlayerInfo playerInfo)
    {
        this(map, controller, playerInfo, ROCKET_SPEED_STEP);
    }

    @Override
    public String friendlyName()
    {
        return "Rocket";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        if (playerInfoOwner == null)
        {
            return new PlayerInfo[0];
        }
        else
        {
            return new PlayerInfo[]{playerInfoOwner};
        }
    }

    @Override
    public void eventLogic(Controller controller)
    {
        // Check if Rocket has expired
        float lifespan = controller.gameTime() - gameTimeCreated;

        if (lifespan > ROCKET_LIFESPAN_MS)
        {
            remove();
        }
        else
        {
            VelocityComponent velocityComponent = (VelocityComponent) components.fetchComponent(VelocityComponent.class);
            Vector2 velocity = velocityComponent.getVelocity();
            float currentSpeed = velocity.length();

            if (currentSpeed < ROCKET_SPEED)
            {
                // Increase speed
                Vector2 increasedSpeed = Vector2.vectorFromAngle(rotation, ROCKET_SPEED_STEP);
                velocity.add(increasedSpeed);

                // Limit to max speed
                velocity.limit(ROCKET_SPEED);
            }
        }

        super.eventLogic(controller);
    }

    @Override
    public void eventCollisionEntity(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        performCollisionExplosion(controller);
    }

    @Override
    public void eventCollisionMap(Controller controller, CollisionMapResult collisionMapResult)
    {
        performCollisionExplosion(controller);
    }

    @Override
    public synchronized void eventDeath(Controller controller, AbstractKiller killer)
    {
        // Do nothing to prevent respawn...
        // TODO: mark for removal here as precaution?
    }

    private synchronized void performCollisionExplosion(Controller controller)
    {
        if (!exploded)
        {
            // Apply damage to entities
            SpatialActions.applyLinearRadiusDamage(controller, this, ROCKET_BLAST_RADIUS, ROCKET_BLAST_DAMAGE, RocketKiller.class);

            // Mark this entity for removal
            remove();

            // Ensure this call never happens again
            exploded = true;
        }
    }

}
