package com.limpygnome.projectsandbox.server.entity.imp.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effect.types.BulletEffect;
import com.limpygnome.projectsandbox.server.effect.types.TracerEffect;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.death.SentryKiller;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.casting.Casting;
import com.limpygnome.projectsandbox.server.entity.physics.casting.CastingResult;
import com.limpygnome.projectsandbox.server.entity.physics.casting.victims.EntityCastVictim;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.RotateResult;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
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

    public Sentry(WorldMap map)
    {
        super(map, (short) 32, (short) 32);

        this.defaultRotation = 0.0f;
        this.lastFired = 0;
        this.physicsStatic = true;
    }

    @Override
    public void logic(Controller controller)
    {
        // Find nearest ent
        List<ProximityResult> ents = DefaultProximity.nearbyEnts(controller, this, RANGE, true, true);

        // Decide on target vector
        Vector2 targetVector;

        if (!ents.isEmpty())
        {
            targetVector = ents.get(0).entity.positionNew;
        }
        else
        {
            // Will cause rotation towards default rotation
            targetVector = null;
        }

        // Move towards target
        RotateResult rotateResult = DefaultProximity.rotateTowardsTarget(
                this, targetVector, ROTATION_RATE, defaultRotation
        );

        // Decide if to fire on target
        if (targetVector != null && Math.abs(rotateResult.getAngleOffsetPostMovement()) < ROTATION_DIFF_FIRE)
        {
            fire(controller);
        }

        // Perform ent logic
        super.logic(controller);
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
            map.effectsManager.add(new BulletEffect(x, y));
        }

        // Show red target tracer
        map.effectsManager.add(new TracerEffect(positionNew, new Vector2(x, y)));
    }

    @Override
    public void eventSpawn(Controller controller, Spawn spawn)
    {
        this.defaultRotation = rotation;

        super.eventSpawn(controller, spawn);
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
