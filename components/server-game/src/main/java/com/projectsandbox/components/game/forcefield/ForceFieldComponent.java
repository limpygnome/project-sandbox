package com.projectsandbox.components.game.forcefield;

import com.projectsandbox.components.game.OwnershipComponent;
import com.projectsandbox.components.game.effect.ExplosionEffect;
import com.projectsandbox.components.game.VelocityComponent;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.component.event.ResetComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.Vertices;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.inventory.InventorySlotState;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * A force-field for protecting an entity.
 *
 * - The force-field is twice the size of the entity.
 * - Any entities within the force-field radius are removed
 */
public class ForceFieldComponent  implements Serializable, EntityComponent, LogicComponentEvent, ResetComponentEvent
{
    private static final long serialVersionUID = 1L;

    // Settings
    private float maxHealth;                    // The maximum health of the force-field
    private float regenStep;                    // The amount the forcefield regenerates each logic cycle
    private long rechargeDelay;                 // The delay before regenerating shields when no health at all
    private float sizeMultiplier;               // The multiplier applied to the size of an entity for the size of the force-field

    // State
    private ForceFieldItem inventoryItem;       // The inventory item to which this belong; can be null, used just for triggering updates
    private float radiusX;                      // The X radius of the current forcefield around the ship
    private float radiusY;                      // The Y radius of the current forcefield around the ship
    private float health;                       // The health of the force-field
    private Vertices shieldVertices;            // The vertices of the force-field, excluding position
    private transient long depletedTime;        // The game time at which the force-field was depleted

    public ForceFieldComponent(ForceFieldItem inventoryItem, float maxHealth, float regenStep, long rechargeDelay, float sizeMultiplier)
    {
        this.inventoryItem = inventoryItem;

        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regenStep = regenStep;
        this.rechargeDelay = rechargeDelay;
        this.sizeMultiplier = sizeMultiplier;
    }

    /**
     * Updates the entity to which this force-field is protecting.
     *
     * @param entity the entity
     */
    public void update(Entity entity)
    {
        // Setup radius size
        this.radiusX = entity.width * sizeMultiplier;
        this.radiusY = entity.height * sizeMultiplier;

        // Build base vertices for forcefield
        shieldVertices = Vertices.buildEllipsis(radiusX, radiusY, 8);
    }

    @Override
    public synchronized void eventLogic(Controller controller, Entity entity)
    {
        // Apply forcefield
        if (isShieldEnabled())
        {
            applyForceField(controller, entity);
        }

        // Regenerate forcefield
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
            // Reset forcefield to fully charged
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

    private void applyForceField(Controller controller, Entity parent)
    {
        // Build vertices with offsets
        Vertices shieldVertices = this.shieldVertices.clone()
                                                     .offset(parent.positionNew)
                                                     .rotate(parent.rotation);

        // Fetch collision detection
        EntityManager entityManager = controller.entityManager;
        CollisionDetection collisionDetection = entityManager.getCollisionDetection();

        // Fetch quad-tree
        WorldMap map = parent.map;
        QuadTree quadTree = map.getEntityMapData().getQuadTree();

        // Perform collision detection between entities and forcefield vertices
        float radius = radiusX > radiusY ? radiusX : radiusY;
        Set<ProximityResult> nearbyEntities = quadTree.getEntitiesWithinRadius(parent, radius);
        CollisionResult collisionResult;

        Entity entityOther;
        Iterator<ProximityResult> iterator = nearbyEntities.iterator();
        ProximityResult proximityResult;

        while (isShieldEnabled() && iterator.hasNext())
        {
            proximityResult = iterator.next();
            entityOther = proximityResult.entity;

            if (parent != entityOther && parent.isCollidable(entityOther))
            {
                collisionResult = collisionDetection.collision(entityOther.cachedVertices, shieldVertices);

                if (collisionResult.collision && isNotOwnedBySamePlayer(parent, entityOther))
                {
                    handleCollision(controller, collisionResult, parent, entityOther);
                }
            }
        }
    }

    private boolean isNotOwnedBySamePlayer(Entity parent, Entity entityOther)
    {
        OwnershipComponent component = (OwnershipComponent) entityOther.components.fetchComponent(OwnershipComponent.class);

        boolean result = component == null || !component.isOwnedBySamePlayer(parent);
        return result;
    }

    private void handleCollision(Controller controller, CollisionResult collisionResult,
                                 Entity parent, Entity entityOther)
    {
        Vector2 collisionPos = entityOther.positionNew.clone().subtract(collisionResult.mtv);

        // Move entity outside of forcefield
        entityOther.positionOffset(collisionResult.mtv);

        // Invert velocity
        VelocityComponent velocityComponent = (VelocityComponent) entityOther.components.fetchComponent(VelocityComponent.class);

        if (velocityComponent != null)
        {
            velocityComponent.getVelocity().invert();
        }

        // Apply damage
        damageFromMass(controller, parent, entityOther, velocityComponent);

        // Create effect at point of impact
        ExplosionEffect effect = new ExplosionEffect(collisionPos.x, collisionPos.y, ExplosionEffect.SubType.FORCE_FIELD);
        controller.effectsManager.add(parent.map, effect);
    }

    /*
        Applies damage to forcefield from mass of entity.
     */
    private void damageFromMass(Controller controller, Entity parent, Entity entityOther, VelocityComponent component)
    {
        if (component != null)
        {
            // Use mass as damage to forcefield
            float damage = component.getMass();

            // Limit damage to total health at most
            if (damage > health)
            {
                damage = health;
            }

            health -= damage;

            // Check if forcefield now depleted
            if (health == 0.0f)
            {
                depletedTime = controller.gameTime();

                // Trigger inventory item update
                if (inventoryItem != null)
                {
                    inventoryItem.slot.setState(InventorySlotState.UPDATED);
                }
            }

            // Apply entity's own mass as damage to its self
            entityOther.damage(controller, parent, damage, ForceFieldKiller.class);
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
