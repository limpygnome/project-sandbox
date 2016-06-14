package com.projectsandbox.components.game.component.shield;

import com.projectsandbox.components.game.component.VelocityComponent;
import com.projectsandbox.components.game.inventory.item.ShieldItem;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.component.event.ResetComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.inventory.InventorySlotState;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * A shield for protecting an entity.
 *
 * - The shield is twice the size of the entity.
 * - Any entities within the shield radius are removed
 */
public class ShieldComponent  implements Serializable, EntityComponent, LogicComponentEvent, ResetComponentEvent
{
    private static final long serialVersionUID = 1L;

    // Settings
    private float maxHealth;                    // The maximum health of the shield
    private float regenStep;                    // The amount the shield regenerates each logic cycle
    private long rechargeDelay;                 // The delay before regenerating shields when no health at all

    // State
    private ShieldItem inventoryItem;           // The inventory item to which this belong; can be null, used just for triggering updates
    private float radiusX;                      // The X radius of the current shield around the ship
    private float radiusY;                      // The Y radius of the current shield around the ship
    private float health;                       // The health of the shield
    private transient Vertices shieldVertices;  // The vertices of the shield
    private transient long depletedTime;        // The game time at which the shield was depleted

    public ShieldComponent(ShieldItem inventoryItem, Entity entity, float maxHealth, float regenStep, long rechargeDelay)
    {
        this.inventoryItem = inventoryItem;

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regenStep = regenStep;
        this.rechargeDelay = rechargeDelay;

        // Radius is currently width and height doubled
        this.radiusX = entity.width * 4.0f;
        this.radiusY = entity.height * 4.0f;
    }

    @Override
    public synchronized void eventLogic(Controller controller, Entity entity)
    {
        // Apply shield
        if (isShieldEnabled())
        {
            applyShield(controller, entity);
        }

        // Regenerate shield
        if (isAbleToRegenerate(controller))
        {
            health += regenStep;

            // Limit to max health
            if (health > maxHealth)
            {
                health = maxHealth;
            }

            // Trigger inventory item update
            if (inventoryItem != null)
            {
                inventoryItem.slot.setState(InventorySlotState.UPDATED);
            }
        }
    }

    @Override
    public void eventReset(Controller controller, Entity entity, boolean respawnAfterPersisted)
    {
        if (!respawnAfterPersisted)
        {
            // Reset shield to fully charged
            health = maxHealth;
            depletedTime = 0;
        }
    }

    private boolean isShieldEnabled()
    {
        return health > 0.0f;
    }

    private boolean isAbleToRegenerate(Controller controller)
    {
        boolean canRegen;

        // Check if depleted and thus cannot regenerate for a period of time
        if (depletedTime > 0)
        {
            if (depletedTime + rechargeDelay < controller.gameTime())
            {
                // Reset depleted time
                depletedTime = 0;
                canRegen = true;
            }
            else
            {
                canRegen = false;
            }
        }
        else
        {
            canRegen = true;
        }

        // Check health not already at max
        if (canRegen)
        {
            canRegen = (health < maxHealth);
        }

        return canRegen;
    }

    private void applyShield(Controller controller, Entity entity)
    {
        // Rebuild shield vertices
        shieldVertices = Vertices.buildEllipsis(entity, radiusX, radiusY, 8);

        EntityManager entityManager = entity.map.entityManager;

        CollisionDetection collisionDetection = entityManager.getCollisionDetection();
        QuadTree quadTree = entityManager.getQuadTree();

        // Perform collision detection between entities and shield vertices
        float radius = radiusX > radiusY ? radiusX : radiusY;
        Set<ProximityResult> nearbyEntities = quadTree.getEntitiesWithinRadius(entity, radius);
        CollisionResult collisionResult;

        Entity entityOther;
        VelocityComponent velocityComponent;
        Iterator<ProximityResult> iterator = nearbyEntities.iterator();
        ProximityResult proximityResult;

        while (isShieldEnabled() && iterator.hasNext())
        {
            proximityResult = iterator.next();
            entityOther = proximityResult.entity;

            if (entity != entityOther && entity.isCollidable(entityOther))
            {
                collisionResult = collisionDetection.collision(entityOther.cachedVertices, shieldVertices);

                if (collisionResult.collision)
                {
                    // Move entity outside of shield
                    entityOther.positionNew.add(collisionResult.mtv);

                    // Invert velocity
                    velocityComponent = (VelocityComponent) entityOther.components.fetchComponent(VelocityComponent.class);

                    if (velocityComponent != null)
                    {
                        velocityComponent.getVelocity().invert();
                    }

                    // Apply damage
                    damageFromMass(controller, entity, entityOther, velocityComponent);
                }
            }
        }
    }

    /*
        Applies damage to shield from mass of entity.
     */
    private void damageFromMass(Controller controller, Entity entity, Entity entityOther, VelocityComponent component)
    {
        if (component != null)
        {
            // Use mass as damage from entity
            float damage = component.getMass();

            if (damage > health)
            {
                health = 0.0f;
            }
            else
            {
                health -= damage;
            }

            // Check if shield now depleted
            if (health == 0.0f)
            {
                depletedTime = controller.gameTime();
            }

            // Apply entity's own mass as damage to its self
            entityOther.damage(controller, entity, damage, ShieldKiller.class);
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
