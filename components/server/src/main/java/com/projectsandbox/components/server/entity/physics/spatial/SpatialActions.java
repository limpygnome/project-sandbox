package com.projectsandbox.components.server.entity.physics.spatial;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.util.CustomMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * A component for performing spatial actions.
 */
public final class SpatialActions
{
    private final static Logger LOG = LogManager.getLogger(SpatialActions.class);

    private SpatialActions() {}

    /**
     * Finds nearby entities and applies damage based on distance from center point.
     *
     * @param radius          The radius of affected entities
     * @param maximumDamage   The maximum damage, linearly applied, relative to distance
     * @param entityCenter    Finds entities near this entity
     */
    public static <T extends Class<? extends AbstractKiller>> void applyLinearRadiusDamage(Controller controller, Entity entityCenter, float radius,
                                                                                           float maximumDamage, T killerType)
    {
        // TODO: test vertices for large objects...
        // Fetch quad-tree
        QuadTree quadTree = entityCenter.map.entityManager.getQuadTree();

        // Find nearby entities
        Set<ProximityResult> proximityResults = quadTree.getEntitiesWithinRadius(entityCenter, radius);

        float damage;
        for (ProximityResult proximityResult : proximityResults)
        {
            // Calculate damage based on distance
            damage = (1.0f - (proximityResult.distance / radius)) * maximumDamage;

            // Apply damage
            if (damage > 0.0f && damage <= maximumDamage)
            {
                proximityResult.entity.damage(controller, entityCenter, damage, killerType);
            }
            else
            {
                LOG.warn("Linear radius damage precision failure - {}", damage);
            }
        }
    }

    /**
     * Rotates towards a given target.
     *
     * @param entity the entity to manipulate
     * @param target The target to rotate towards; can be null to rotate towards default rotation
     * @param rotateRate the maximum rotation step per cycle/call to this method
     * @param defaultRotation the default rotation for when target is null
     * @return
     */
    public static RotateResult rotateTowardsTarget(Entity entity, Vector2 target, float rotateRate, float defaultRotation)
    {
        // Lock entity since we need changes to be atomic
        synchronized (entity)
        {
            RotateResult rotateResult = new RotateResult();

            // Compute rotation offset needed to point towards target or default rotation
            float targetAngleOffset;

            if (target != null)
            {
                targetAngleOffset = Vector2.computeTargetAngleOffset(entity, target);
            }
            else
            {
                targetAngleOffset = CustomMath.clampAngle(defaultRotation - entity.rotation);
            }

            rotateResult.setAngleOffset(targetAngleOffset);

            // Check if to not rotate towards target
            if (rotateRate == 0.0f)
            {
                rotateResult.setAngleOffsetPostMovement(targetAngleOffset);
            }
            else
            {
                float targetAngleOffsetAbs = Math.abs(targetAngleOffset);

                // If offset less than rate, just rotate to align...
                float rotateOffset;

                if (targetAngleOffsetAbs < rotateRate)
                {
                    rotateOffset = targetAngleOffset;
                }
                else if (targetAngleOffset < 0.0f)
                {
                    rotateOffset = -rotateRate;
                }
                else if (targetAngleOffset > 0.0f)
                {
                    rotateOffset = rotateRate;
                }
                else
                {
                    rotateOffset = 0.0f;
                }

                // Apply offset for current step
                entity.rotationOffset(rotateOffset);
                rotateResult.setAngleOffsetPostMovement(targetAngleOffset - rotateOffset);
            }

            return rotateResult;
        }
    }

}
