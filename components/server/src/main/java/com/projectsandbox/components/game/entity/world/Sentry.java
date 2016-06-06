package com.projectsandbox.components.game.entity.world;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.effect.types.BulletEffect;
import com.projectsandbox.components.server.effect.types.TracerEffect;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.death.SentryKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.casting.Casting;
import com.projectsandbox.components.server.entity.physics.casting.CastingResult;
import com.projectsandbox.components.server.entity.physics.casting.victims.EntityCastVictim;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.physics.spatial.RotateResult;
import com.projectsandbox.components.server.entity.physics.spatial.SpatialActions;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

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
    public void eventLogic(Controller controller)
    {
        // Fetch quad-tree
        QuadTree quadTree = map.entityManager.getQuadTree();

        // Find nearest entity
        Set<ProximityResult> nearbyEntities = quadTree.getEntitiesWithinRadius(this, RANGE);
        ProximityResult nearestResult = null;

        for (ProximityResult proximityResult : nearbyEntities)
        {
            if (nearestResult == null || proximityResult.distance < nearestResult.distance)
            {
                nearestResult = proximityResult;
            }
        }

        // Decide on target vector
        Vector2 targetVector;

        if (nearestResult != null)
        {
            targetVector = nearestResult.entity.positionNew;
        }
        else
        {
            // Will cause rotation towards default rotation
            targetVector = null;
        }

        // Move towards target
        RotateResult rotateResult = SpatialActions.rotateTowardsTarget(
                this, targetVector, ROTATION_RATE, defaultRotation
        );

        // Decide if to fire on target
        if (targetVector != null && Math.abs(rotateResult.getAngleOffsetPostMovement()) < ROTATION_DIFF_FIRE)
        {
            fire(controller);
        }

        // Perform ent logic
        super.eventLogic(controller);
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
    public synchronized void eventReset(Controller controller, Spawn spawn, boolean respawnAfterPersisted)
    {
        super.eventReset(controller, spawn, respawnAfterPersisted);

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
