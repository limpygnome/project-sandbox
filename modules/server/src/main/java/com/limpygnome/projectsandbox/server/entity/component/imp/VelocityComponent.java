package com.limpygnome.projectsandbox.server.entity.component.imp;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.component.EntityComponent;
import com.limpygnome.projectsandbox.server.entity.component.event.CollisionEntityComponentEvent;
import com.limpygnome.projectsandbox.server.entity.component.event.LogicComponentEvent;
import com.limpygnome.projectsandbox.server.entity.component.event.ResetComponentEvent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;

/**
 * Used to apply zero gravity to entities.
 */
public class VelocityComponent implements EntityComponent, CollisionEntityComponentEvent, LogicComponentEvent, ResetComponentEvent
{
    private float mass;
    private float velocityX;
    private float velocityY;

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        entity.positionOffset(velocityX, velocityY);
    }

    @Override
    public void eventHandleCollisionEntity(Controller controller, Entity entity, Entity entityOther, CollisionResult result)
    {
        float speedUs = Math.abs(entity.getSpeed());
        float speedOther = Math.abs(entityOther.getSpeed());

        Vector2 velocityUs = Vector2.vectorFromAngle(entity.rotation, speedUs);
        Vector2 velocityOther = Vector2.vectorFromAngle(entityOther.rotation, speedOther);

        float collisionVelocityX = Math.abs(velocityUs.x - velocityOther.x);
        float collisionVelocityY = Math.abs(velocityUs.y - velocityOther.y);
        float collisionSpeed = Vector2.length(new Vector2(collisionVelocityX, collisionVelocityY));

        // Invert our velocity
        // TODO: finish this, need to distribute speed as ratio using mass
        velocityX = -velocityX;
        velocityY = -velocityY;
    }

    @Override
    public void eventReset(Controller controller, Entity entity)
    {
        velocityX = 0.0f;
        velocityY = 0.0f;
    }

}
