package com.limpygnome.projectsandbox.server.entity.physics.collisions;

import com.limpygnome.projectsandbox.server.entity.Entity;

import java.util.Collection;

/**
 * Used to perform collision detection.
 */
public interface CollisionDetection
{

    /**
     * Performs collision detection between two entities.
     *
     * @param a the original entity
     * @param b the second entity
     * @return the result from testing the two entities
     */
    CollisionResult collision(Entity a, Entity b);

    /**
     * Used to perform collision detection with a map.
     *
     * Currently only tile map is supported.
     *
     * @param entity the entity to be tested
     * @return a collection of collision results
     */
    Collection<CollisionResultMap> collisionMap(Entity entity);

}
