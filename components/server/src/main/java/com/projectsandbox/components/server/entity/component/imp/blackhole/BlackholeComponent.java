package com.projectsandbox.components.server.entity.component.imp.blackhole;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;

import java.util.Set;

/**
 * A component for pulling other entities towards the center of an entity, with acceleration increasing with shorter
 * the distance.
 */
public class BlackholeComponent implements EntityComponent, LogicComponentEvent
{
    private float maxAcceleration;
    private float maxDamage;
    private float radius;

    public BlackholeComponent(float maxAcceleration, float maxDamage, float radius)
    {
        this.maxAcceleration = maxAcceleration;
        this.maxDamage = maxDamage;
        this.radius = radius;
    }

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        // Fetch nearby entities
        QuadTree quadTree = entity.map.entityManager.getQuadTree();
        Set<ProximityResult> nearbyEntities = quadTree.getEntitiesWithinRadius(entity, radius);

        float distance;
        Entity entityOther;

        float distanceMultiplier;
        float acceleration;
        float damage;
        float angle;
        Vector2 accelerationVelocity;

        for (ProximityResult proximityResult : nearbyEntities)
        {
            entityOther = proximityResult.entity;

            if (entityOther != entity)
            {
                distance = proximityResult.distance;
                distanceMultiplier = distance / radius;

                // Calculate linear acceleration and angle in which entity is accelerated towards our center point
                acceleration = distanceMultiplier * maxAcceleration;
                angle = Vector2.computeTargetAngleOffset(entityOther, entity.positionNew);
                accelerationVelocity = Vector2.vectorFromAngle(angle, acceleration);

                // Accelerate the entity
                entityOther.positionOffset(accelerationVelocity);

                // Apply linear damage
                damage = distanceMultiplier * maxDamage;
                entityOther.damage(controller, entity, damage, BlackholeKiller.class);
            }
        }
    }

}
