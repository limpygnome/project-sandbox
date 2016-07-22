package com.projectsandbox.components.game.jumpdrive;

import com.projectsandbox.components.game.effect.ExplosionEffect;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionDetection;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.inventory.InventorySlotState;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Set;

/**
 * Allows the player to instantly move around the map.
 */
@InventoryItemTypeId(typeId = 800)
public class JumpDriveItem extends AbstractInventoryItem
{
    public static final long serialVersionUID = 1L;

    /*
        Minimum delay between jumps.
     */
    private static final long MINIMUM_DELAY_MS = 5000;

    // Settings
    private float maxDistance;          // The maximum jump distance
    private float distanceStep;         // The additional jump distance the ship will jump when the player has the item on
    private float rechargeStep;         // The amount the drive recharges each logic cycle

    // State
    private float rechargedDistance;    // The distance currently recharged and available to use
    private float jumpDistance;         // The distance to be jumped
    private long lastUsed;              // Time at which jump was last used; prevents spam

    public JumpDriveItem(float maxDistance, float distanceStep, float rechargeStep)
    {
        this.maxDistance = maxDistance;
        this.distanceStep = distanceStep;
        this.rechargeStep = rechargeStep;

        rechargedDistance = 0.0f;
        jumpDistance = 0.0f;
        lastUsed = 0L;

        // Setup type to be toggled
        this.invokeType = InventoryInvokeType.TOGGLE;
    }

    @Override
    public void logic(Controller controller)
    {
        // Execute logic to determine state
        super.logic(controller);

        boolean changed = false;
        long lastUsedMs = controller.gameTime() - lastUsed;

        if(lastUsedMs >= MINIMUM_DELAY_MS)
        {
            switch (slot.invokeState)
            {
                case OFF:

                    // Check if any jump distance built up and able to use
                    if (jumpDistance > 0)
                    {
                        Entity parent = slot.inventory.parent;

                        // Update when last used (now)...
                        lastUsed = controller.gameTime();

                        // Jump the parent...
                        Vector2 jumpOffset = Vector2.vectorFromAngle(parent.rotation, jumpDistance);
                        parent.positionNew.add(jumpOffset);

                        // Create explosion effect
                        ExplosionEffect effect = new ExplosionEffect(
                                parent.positionNew.x, parent.positionNew.y, ExplosionEffect.SubType.JUMP_DRIVE
                        );
                        controller.effectsManager.add(parent.map, effect);

                        // Fetch collision detection
                        EntityManager entityManager = controller.entityManager;
                        CollisionDetection collisionDetection = entityManager.getCollisionDetection();

                        // Fetch nearby entities
                        WorldMap map = parent.map;
                        QuadTree quadTree = map.getEntityMapData().getQuadTree();
                        Set<Entity> nearbyEntities = quadTree.getCollidableEntities(parent);

                        // Fetch any possible collisions
                        CollisionResult collisionResult;

                        for (Entity entity : nearbyEntities)
                        {
                            if (entity != parent && parent.isCollidable(entity))
                            {
                                collisionResult = collisionDetection.collision(parent, entity);

                                if (collisionResult.collision)
                                {
                                    // Cause parent to explode for dodgy jump...
                                    parent.kill(controller, entity, JumpDriveKiller.class);
                                    break;
                                }
                            }
                        }

                        // Reset jump distance
                        jumpDistance = 0.0f;
                    }

                    break;
                case ON:

                    // Increase jump distance, if any recharge available...
                    if (rechargedDistance > 0)
                    {
                        // Scrape any available recharge distance...
                        if (distanceStep > rechargedDistance)
                        {
                            jumpDistance += rechargedDistance;
                            rechargedDistance = 0;
                        }
                        else
                        {
                            jumpDistance += distanceStep;
                            rechargedDistance -= distanceStep;
                        }

                        // Limit jump distance to maximum jump distance
                        if (jumpDistance > maxDistance)
                        {
                            jumpDistance = maxDistance;
                        }
                    }

                    break;
            }

            // Check if drive needs to recharge...
            if (rechargedDistance != maxDistance)
            {
                float newRechargedDistance = rechargedDistance + rechargeStep;

                // Limit to maximum jump distance
                if (newRechargedDistance > maxDistance)
                {
                    newRechargedDistance = maxDistance;
                }

                // Set new recharged distance
                rechargedDistance = newRechargedDistance;
                changed = true;
            }
        }

        // Check if to change state
        if (changed)
        {
            slot.setState(InventorySlotState.UPDATED);
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        float percentRecharged = (rechargedDistance / maxDistance) * 100.0f;
        return String.format("%.0f%%", percentRecharged);
    }

}
