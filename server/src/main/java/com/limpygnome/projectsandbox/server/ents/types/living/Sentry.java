package com.limpygnome.projectsandbox.server.ents.types.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effects.types.BulletEffect;
import com.limpygnome.projectsandbox.server.effects.types.TracerEffect;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.death.SentryKiller;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.casting.Casting;
import com.limpygnome.projectsandbox.server.ents.physics.casting.CastingResult;
import com.limpygnome.projectsandbox.server.ents.physics.casting.victims.EntityCastVictim;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by limpygnome on 12/05/15.
 */
@EntityType(typeId = 500, typeName = "living/sentry")
public class Sentry extends Entity
{
    private final static Logger LOG = LogManager.getLogger(Sentry.class);

    public final static float RANGE = 150.0f;
    public final static float ROTATION_RATE = 0.1f;
    public final static float ROTATION_DIFF_FIRE = 0.26f;
    public final static float FIRE_RATE_MS = 180.0f;
    public final static float BULLET_DAMAGE = 20.0f;

    public float defaultRotation;
    private long lastFired;

    public Sentry()
    {
        super((short) 32, (short) 32);

        this.defaultRotation = 0.0f;
        this.lastFired = 0;
        this.physicsStatic = true;
    }

    @Override
    public void logic(Controller controller)
    {
        // Find nearest ent
        List<ProximityResult> ents = DefaultProximity.nearbyEnts(controller, this, RANGE, true, true);

        if (!ents.isEmpty())
        {
            ProximityResult result = ents.get(0);

            float targetAngleOffset = Vector2.angleToFaceTarget(positionNew, rotation, result.entity.positionNew);

            rotateToTarget(controller, targetAngleOffset, true);
        }
        else
        {
            // Rotate towards default rotation
            rotateToTarget(controller, defaultRotation - rotation, false);
        }

        // Perform ent logic
        super.logic(controller);
    }

    private void rotateToTarget(Controller controller, float targetAngleOffset, boolean fireMode)
    {
        float targetAngleOffsetAbs = Math.abs(targetAngleOffset);

        // Rotate towards target
        if (targetAngleOffsetAbs < ROTATION_RATE)
        {
            rotationOffset(targetAngleOffset);
        }
        else if (targetAngleOffset < 0.0f)
        {
            rotationOffset(-ROTATION_RATE);
        }
        else if (targetAngleOffset > 0.0f)
        {
            rotationOffset(ROTATION_RATE);
        }

        // Check if we can fire and  we're within the right angle to fire
        if (fireMode && targetAngleOffsetAbs <= ROTATION_DIFF_FIRE)
        {
            fire(controller);
        }
    }

    private void fire(Controller controller)
    {
        long currTime = System.currentTimeMillis();

        // Check we can fire
        if (currTime - lastFired < FIRE_RATE_MS)
        {
            return;
        }
        lastFired = currTime;

        // Cast bullet
        CastingResult castingResult = Casting.cast(controller, this, rotation, RANGE);
        float x = castingResult.x;
        float y = castingResult.y;

        // Check we hit an entity
        if (castingResult.collision && castingResult.victim instanceof EntityCastVictim)
        {
            // Inflict damage
            EntityCastVictim castVictim = (EntityCastVictim) castingResult.victim;
            castVictim.entity.damage(controller, this, BULLET_DAMAGE, SentryKiller.class);

            // Create bullet effect
            controller.effectsManager.add(new BulletEffect(x, y));
        }

        // Show red target tracer
        controller.effectsManager.add(new TracerEffect(positionNew, new Vector2(x, y)));
    }

    @Override
    public void eventSpawn(Controller controller)
    {
        this.defaultRotation = rotation;
    }

    @Override
    public String friendlyName()
    {
        return "Sentry";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }
}
