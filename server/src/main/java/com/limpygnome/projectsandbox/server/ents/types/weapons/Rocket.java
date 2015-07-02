package com.limpygnome.projectsandbox.server.ents.types.weapons;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.death.RocketKiller;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResultMap;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.limpygnome.projectsandbox.server.constants.weapons.RocketConstants.*;

/**
 * An Rocket fired by a rocket weapon.
 */
@EntityType(typeId = 600)
public class Rocket extends Entity
{
    private final static Logger LOG = LogManager.getLogger(Rocket.class);

    private PlayerInfo playerInfoOwner;
    private long gameTimeCreated;
    private float speedStep;

    public Rocket(Controller controller, PlayerInfo playerInfoOwner, float initialSpeed)
    {
        super((short) 9, (short) 12);

        this.playerInfoOwner = playerInfoOwner;
        this.gameTimeCreated = controller.gameTime();
        this.speedStep = initialSpeed;

        setMaxHealth(10);
    }

    public Rocket(Controller controller, PlayerInfo playerInfo)
    {
        this(controller, playerInfo, ROCKET_SPEED_STEP);
    }

    @Override
    public String friendlyName()
    {
        return "Rocket";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[]{ playerInfoOwner };
    }

    @Override
    public void logic(Controller controller)
    {
        // Check if Rocket has expired
        float lifespan = controller.gameTime() - gameTimeCreated;

        if (lifespan > ROCKET_LIFESPAN_MS)
        {
            remove();
        }

        // Check if to increment rocket speed
        if (this.speedStep < ROCKET_SPEED)
        {
            // Increment by speed step, towards reaching max speed
            this.speedStep += ROCKET_SPEED_STEP;

            // Check step has not exceeded max speed
            if (this.speedStep > ROCKET_SPEED)
            {
                this.speedStep = ROCKET_SPEED;
            }
        }

        // Move rocket
        Vector2 offset = Vector2.vectorFromAngle(rotation, speedStep);
        positionOffset(offset);

        super.logic(controller);
    }

    @Override
    public void eventHandleCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        performCollisionExplosion(controller);
    }

    @Override
    public void eventHandleCollisionMap(Controller controller, CollisionResultMap collisionResultMap)
    {
        performCollisionExplosion(controller);
    }

    private void performCollisionExplosion(Controller controller)
    {
        // Apply damage to entities
        DefaultProximity.applyLinearRadiusDamage(controller, this, ROCKET_BLAST_RADIUS, ROCKET_BLAST_DAMAGE, true, RocketKiller.class);

        // Mark this entity for removal
        remove();
    }

}
