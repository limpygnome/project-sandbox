package com.limpygnome.projectsandbox.server.entity.component;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;

/**
 * Used to apply zero gravity to entities.
 */
public class ZeroGravityComponent implements EntityComponent
{
    private float mass;
    private float velocityX;
    private float velocityY;

    /*

        need a way to handle event callbacks such as logic, collision etc. could make each an interface, then implement
        interface here. then check if a component has interface, then call. lots of inefficient iteration though.
     */

    @Override
    public void logic(Entity entity, Controller controller)
    {
        entity.positionOffset(velocityX, velocityY);
    }

    public synchronized void eventHandleCollision(Entity entity, Entity entityOther, CollisionResult result)
    {
        float speedUs = Math.abs(entity.getSpeed());
        float speedOther = Math.abs(entityOther.getSpeed());

        Vector2 velocityUs = Vector2.vectorFromAngle(entity.rotation, speedUs);
        Vector2 velocityOther = Vector2.vectorFromAngle(entityOther.rotation, speedOther);

        float collisionVelocityX = Math.abs(velocityUs.x - velocityOther.x);
        float collisionVelocityY = Math.abs(velocityUs.y - velocityOther.y);
        float collisionSpeed = Vector2.length(new Vector2(collisionVelocityX, collisionVelocityY));

        // Invert our velocity
        velocityX = -velocityX;
        velocityY = -velocityY;
    }

}
