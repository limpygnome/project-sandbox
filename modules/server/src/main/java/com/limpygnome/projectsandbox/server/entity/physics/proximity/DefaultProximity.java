package com.limpygnome.projectsandbox.server.entity.physics.proximity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.util.CustomMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: refactor this class to use a quadtree.
 *
 * TOOD: refactor for spring to inject this for a map/entity manager, rather than static methods, ew...
 */
@Deprecated
public class DefaultProximity
{
    private final static Logger LOG = LogManager.getLogger(DefaultProximity.class);


    /**
     * Finds nearby entities and applies damage based on distance from center point.
     *
     * @param radius          The radius of affected entities
     * @param maximumDamage   The maximum damage, linearly applied, relative to distance
     * @param entityCenter    Finds entities near this entity
     * @param testAllVertices
     */
    public static <T extends Class<? extends AbstractKiller>> void applyLinearRadiusDamage(Controller controller, Entity entityCenter, float radius,
                                                                                           float maximumDamage, boolean testAllVertices, T killerType)
    {
        List<ProximityResult> proximityResults = nearbyEnts(controller, entityCenter, radius, testAllVertices, false);

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

    public static List<ProximityResult> nearbyEnts(Controller controller, Entity a, float distance, boolean testAllVertices, boolean sortList)
    {
        LinkedList<ProximityResult> result = new LinkedList<>();

        synchronized (a.map.entityManager)
        {
            Entity b;
            float entDistance;

            for (Map.Entry<Short, Entity> kv : a.map.entityManager.getEntities().entrySet())
            {
                b = kv.getValue();

                if (a != b && !b.isDead() && !b.isDeleted())
                {
                    // Get distance to center
                    entDistance = Vector2.distance(a.positionNew, b.positionNew);

                    // Check if we're only performing a simple  test
                    if (!testAllVertices)
                    {
                        if (entDistance <= distance)
                        {
                            result.add(new ProximityResult(b, distance));
                        }
                    }
                    else if (entDistance <= distance + b.cachedVertices.collisionRadius)
                    {
                        // Test all the vertices - expensive!
                        // -- First find the closest vertex to the center
                        Vector2 closestVertex = null;
                        float closestDistance = 0.0f;
                        int i;

                        for (i = 0; i < a.cachedVertices.vertices.length; i++)
                        {
                            entDistance = Vector2.distance(a.cachedVertices.vertices[i], b.positionNew);

                            if (closestVertex == null || entDistance < closestDistance)
                            {
                                closestVertex = a.cachedVertices.vertices[i];
                                closestDistance = entDistance;
                            }
                        }

                        // -- Find closest vertex of ent being tested
                        if (closestVertex != null)
                        {
                            Vector2 closestVertexB = null;
                            for (i = 0; i < b.cachedVertices.vertices.length; i++)
                            {
                                entDistance = Vector2.distance(closestVertex, b.cachedVertices.vertices[i]);

                                if (closestVertexB == null || entDistance < closestDistance)
                                {
                                    closestVertexB = b.cachedVertices.vertices[i];
                                    closestDistance = entDistance;
                                }
                            }

                            // Check we found a result
                            if (closestVertexB != null)
                            {
                                result.add(new ProximityResult(b, closestDistance, closestVertex, closestVertexB));
                            }
                        }
                    }
                }
            }
        }

        // Check if to sort list
        if (sortList)
        {
            // TODO: consider performance of call versus sorting on insert versus our own sorting algorithm, this is probably best though
            Collections.sort(result);
        }

        return result;
    }

    public RotateResult rotateTowardsTarget(Entity entity, Entity target, float rotateRate, float defaultRotation)
    {
        return rotateTowardsTarget(entity, target.positionNew, rotateRate, defaultRotation);
    }

    /**
     * Computes angle offset between entity and target vector.
     *
     * @param entity source entity
     * @param target the target vector
     * @return the rotation between entity's rotation and target vector
     */
    public static float computeTargetAngleOffset(Entity entity, Vector2 target)
    {
        return Vector2.angleToFaceTarget(
                entity.positionNew, entity.rotation, target
        );
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
                targetAngleOffset = computeTargetAngleOffset(entity, target);
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
