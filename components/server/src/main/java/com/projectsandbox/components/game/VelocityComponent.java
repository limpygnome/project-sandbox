package com.projectsandbox.components.game;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.CollisionEntityComponentEvent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.component.event.ProjectInFrontOfEntityEvent;
import com.projectsandbox.components.server.entity.component.event.ResetComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;

import java.io.Serializable;

/**
 * Used to apply zero gravity to entities.
 */
public class VelocityComponent implements Serializable, EntityComponent, CollisionEntityComponentEvent, LogicComponentEvent, ResetComponentEvent, ProjectInFrontOfEntityEvent
{
    private static final long serialVersionUID = 1L;

    private Vector2 velocity;
    private Vector2 initialVelocity;
    private float mass;

    public VelocityComponent(float mass)
    {
        this.initialVelocity = null;
        this.velocity = new Vector2();
        this.mass = mass;
    }

    @Override
    public synchronized void eventLogic(Controller controller, Entity entity)
    {
        entity.positionOffset(velocity.x, velocity.y);
    }

    @Override
    public synchronized void eventCollisionEntity(Controller controller, Entity entity, Entity entityOther, CollisionResult result)
    {
        // Fetch velocity component of other entity
        VelocityComponent componentOther = (VelocityComponent) entityOther.components.fetchComponent(VelocityComponent.class);

        if (componentOther != null && entity.isCollidable(entityOther))
        {
            Vector2 velocityOther = componentOther.velocity;

            float massOther = componentOther.getMass();
            float totalMass = mass + massOther;

            float vx = (velocity.x + velocityOther.x) / totalMass;
            float vy = (velocity.y + velocityOther.y) / totalMass;

            float vxMass = vx * massOther;
            float vyMass = vy * massOther;
            velocity.set(vxMass, vyMass);

            float vxOtherMass = vx * mass;
            float vyOtherMass = vy * mass;
            velocityOther.set(vxOtherMass, vyOtherMass);
        }
        else
        {
            // Simply invert our velocity so we bounce; may consider dampening in the future
            velocity.invert();
        }
    }

    @Override
    public synchronized void eventReset(Controller controller, Entity entity, boolean respawnAfterPersisted)
    {
        if (!respawnAfterPersisted)
        {
            if (initialVelocity != null)
            {
                velocity = initialVelocity;
                initialVelocity = null;
            }
            else
            {
                velocity.set(0.0f, 0.0f);
            }
        }
    }

    @Override
    public void projectInFrontOfEntity(Entity entity, Entity parent, float spacing, Vector2 newPosition)
    {
        // Check if parent has velocity to match
        VelocityComponent velocityComponent = (VelocityComponent) parent.components.fetchComponent(VelocityComponent.class);

        if (velocityComponent != null)
        {
            // Match new position with parent velocity; apply twice for safety
            newPosition.add(velocityComponent.velocity);
            newPosition.add(Vector2.vectorFromAngle(parent.rotation, 50.0f));

            // Set our velocity to match parent
            this.velocity = velocityComponent.velocity.clone();
            this.initialVelocity = this.velocity.clone();
        }
    }

    public synchronized float getMass()
    {
        return mass;
    }

    /**
     * NOTE: you should synchronize around this component when performing changes for thread safety.
     *
     * @return
     */
    public synchronized Vector2 getVelocity()
    {
        return velocity;
    }

}
