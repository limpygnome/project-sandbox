package com.projectsandbox.components.game.component.shield;

import com.projectsandbox.components.game.component.VelocityComponent;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;

import java.io.Serializable;
import java.util.Set;

/**
 * A shield for protecting an entity.
 *
 * - The shield is twice the size of the entity.
 * - Any entities within the shield radius are removed
 */
public class ShieldComponent  implements Serializable, EntityComponent, LogicComponentEvent
{
    private static final long serialVersionUID = 1L;

    // Settings
    private float maxHealth;    // The maximum health of the shield
    private float regenStep;    // The amount the shield regenerates each logic cycle

    // State
    private float radiusX;      // The X radius of the current shield around the ship
    private float radiusY;      // The Y radius of the current shield around the ship
    private float health;       // The health of the shield
    private transient Vertices shieldVertices;    // The vertices of the shield

    public ShieldComponent(Entity entity, float maxHealth, float regenStep)
    {
        this.maxHealth = maxHealth;
        this.regenStep = regenStep;

        // Radius is currently width and height doubled
        this.radiusX = entity.width * 4.0f;
        this.radiusY = entity.height * 4.0f;
    }

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        // Rebuild shield vertices
        shieldVertices = Vertices.buildEllipsis(entity, radiusX, radiusY, 16);

        EntityManager entityManager = entity.map.entityManager;

        CollisionDetection collisionDetection = entityManager.getCollisionDetection();
        QuadTree quadTree = entityManager.getQuadTree();

        // Perform collision detection between entities and shield vertices
        float radius = radiusX > radiusY ? radiusX : radiusY;
        Set<ProximityResult> entities = quadTree.getEntitiesWithinRadius(entity, radius);
        CollisionResult collisionResult;

        Entity entityOther;
        for (ProximityResult proximityResult : entities)
        {
            entityOther = proximityResult.entity;

            if (entity != entityOther && entity.isCollidable(entityOther))
            {
                collisionResult = collisionDetection.collision(entityOther, shieldVertices);

                if (collisionResult.collision)
                {
                    // Move entity outside of shield
                    entityOther.positionNew.add(collisionResult.mtv);

                    // Apply damage
                    damageFromMass(controller, entityOther, entity);
                }
            }
        }
    }

    /*
        Applies damage to shield from mass of entity.
     */
    private void damageFromMass(Controller controller, Entity entity, Entity entityInflicter)
    {
        float damageLeft = 0.0f;
        VelocityComponent component = (VelocityComponent) entity.components.fetchSingle(VelocityComponent.class);

        if (component != null)
        {
            // Use mass as damage from entity
            float damage = 0.0f;//component.getMass();

            if (damage > health)
            {
                damageLeft = damage - health;
                health = 0.0f;
            }
            else
            {
                health -= damage;
            }
        }

        // Apply any left over damage
        if (damageLeft > 0.0f)
        {
            entity.damage(controller, entityInflicter, damageLeft, ShieldKiller.class);
        }
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public float getHealth()
    {
        return health;
    }

}
