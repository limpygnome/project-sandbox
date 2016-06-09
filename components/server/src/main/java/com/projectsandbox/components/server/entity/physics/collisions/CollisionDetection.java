package com.projectsandbox.components.server.entity.physics.collisions;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;

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
     * Performs collision detection between an entity and a set of vertices.
     *
     * @param entity
     * @param vertices
     * @return
     */
    CollisionResult collision(Entity entity, Vertices vertices);

    /**
     * Used to perform collision detection with a map.
     *
     * Currently only tile map is supported.
     *
     * @param entity the entity to be tested
     * @return a collection of collision results
     */
    Collection<CollisionMapResult> collisionMap(Entity entity);

}
