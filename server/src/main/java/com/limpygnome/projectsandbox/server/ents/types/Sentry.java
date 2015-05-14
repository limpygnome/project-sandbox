package com.limpygnome.projectsandbox.server.ents.types;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effects.types.BulletEffect;
import com.limpygnome.projectsandbox.server.effects.types.TracerEffect;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.annotations.EntityType;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.physics.casting.Casting;
import com.limpygnome.projectsandbox.server.ents.physics.casting.CastingResult;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.ents.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.utils.CustomMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 12/05/15.
 */
@EntityType(typeId = 500)
public class Sentry extends Entity
{
    private final static Logger LOG = LogManager.getLogger(Sentry.class);

    public final static float RANGE = 300.0f;
    public final static float ROTATION_RATE = 0.1f;

    public float defaultRotation;

    public Sentry()
    {
        super((short) 32, (short) 32);

        this.defaultRotation = 0.0f;
    }

    @Override
    public void logic(Controller controller)
    {
        // Find nearest ent
        List<ProximityResult> ents = DefaultProximity.nearbyEnts(controller, this, RANGE, true, true);

        if (!ents.isEmpty())
        {
            ProximityResult result = ents.get(0);

//            LOG.debug("locked onto " + result.entity.id);

            Vector2 b = result.entity.positionNew;
            Vector2 a = positionNew;

            // Compute rotation towards ent
            float rotation = (float) -(Math.atan2(a.y - b.y, a.x - b.x) + CustomMath.PI_FLOAT_HALF);
//            rotation -= CustomMath.PI_FLOAT;
//            rotation = CustomMath.clampRepeat(
//                       -CustomMath.PI_FLOAT,
//                    CustomMath.PI_FLOAT,
//                    rotation
//            );

            LOG.debug("ent: {}, rotation: {}", result.entity.id, rotation);

            // Rotate towards ent
            if (moveToRotation(controller, rotation))
            {
                // Fire at ent
                fire(result.entity);
            }
        }
        else
        {
            // Rotate towards default rotation
            //moveToRotation(defaultRotation);
        }

        // Fire on ent if sentry aligns with target

        // Perform ent logic
        super.logic(controller);
    }

    private boolean moveToRotation(Controller controller, float rotationTarget)
    {
        float differenceToTarget = rotationTarget - rotation;

        float clampAmount = CustomMath.PI_FLOAT;
        float rotationOffset = CustomMath.clampRepeat(-clampAmount, clampAmount, differenceToTarget);

        // Now clamp for gradual rotation

        rotationOffset(rotationOffset);

        CastingResult castingResult = Casting.cast(controller, this, rotation, RANGE);

        float x = castingResult.x;
        float y = castingResult.y;
        controller.effectsManager.add(new BulletEffect(x, y));
        controller.effectsManager.add(new TracerEffect(positionNew, new Vector2(x, y)));





        // Decide which way to rotate
//        float remainingLeft = Math.abs(
//                CustomMath.clampRepeat(
//                        -CustomMath.PI_FLOAT,
//                        CustomMath.PI_FLOAT,
//                        rotation - rotationTarget
//                )
//        );
//        float remainingRight = Math.abs(
//                CustomMath.clampRepeat(
//                        -CustomMath.PI_FLOAT,
//                        CustomMath.PI_FLOAT,
//                        rotation + rotationTarget
//                )
//        );

//        LOG.debug("r1 {}, r2 {}", remainingLeft, remainingRight);

//        float rotateAmount = ROTATION_RATE;

        // Invert rate if going left / anti-clockwise
//        if (remainingLeft < remainingRight)
//        {
//            rotateAmount *= -1;
//        }

        // Check if the rotation amount is smaller than rate
//        float remainingAfter = Math.abs(
//                CustomMath.clampRepeat(
//                        -CustomMath.PI_FLOAT,
//                        CustomMath.PI_FLOAT,
//                        rotationTarget - (rotation + rotateAmount)
//                )
//        );

//        if (remainingAfter < rotateAmount)
//        {
//            rotateAmount = remainingAfter;
//
//            if (remainingLeft < remainingRight)
//            {
//                rotateAmount *= -1;
//            }
//        }

        // Update rotation
        //rotationOffset(rotateAmount);

//        LOG.debug("rotation amount {} " + rotateAmount);

        return rotation == rotationTarget;
    }

    private void fire(Entity target)
    {
    }

    @Override
    public void eventSpawn()
    {
        this.defaultRotation = rotation;
    }

    @Override
    public void reset()
    {
    }
}
