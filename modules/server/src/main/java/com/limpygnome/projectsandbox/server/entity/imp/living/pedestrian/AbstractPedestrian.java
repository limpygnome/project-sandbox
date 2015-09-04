package com.limpygnome.projectsandbox.server.entity.imp.living.pedestrian;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.imp.living.Player;
import com.limpygnome.projectsandbox.server.entity.imp.vehicle.AbstractVehicle;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.casting.Casting;
import com.limpygnome.projectsandbox.server.entity.physics.casting.CastingResult;
import com.limpygnome.projectsandbox.server.entity.physics.casting.victims.EntityCastVictim;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Node;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.InventoryInvokeState;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

import java.util.List;

import static com.limpygnome.projectsandbox.server.constant.entity.PedestrianConstants.*;

/**
 * Used as a base class for implementing pedestrians, which behave similar to players.
 */
public abstract class AbstractPedestrian extends Entity
{
    private Entity targetEntity;
    private Inventory inventory;

    private float engageDistance;
    private float followSpeed;
    private float followDistance;
    private float attackDistance;
    private float attackRotationNoise;

    private Path lastPathFound;
    private int lastPathOffset;

    public AbstractPedestrian(short width, short height, float health, Class[] inventoryItems, float engageDistance,
                              float followSpeed, float followDistance, float attackDistance, float attackRotationNoise)
    {
        super(width, height);

        setMaxHealth(health);

        this.targetEntity = null;

        // Setup inventory
        if (inventoryItems != null && inventoryItems.length > 0)
        {
            this.inventory = new Inventory(this);
            this.inventory.add(inventoryItems);
        }
        else
        {
            this.inventory = null;
        }

        this.engageDistance = engageDistance;
        this.followSpeed = followSpeed;
        this.followDistance = followDistance;
        this.attackDistance = attackDistance;
        this.attackRotationNoise = attackRotationNoise;
    }

    @Override
    public synchronized void logic(Controller controller)
    {
        // Run inventory logic
        if (this.inventory != null)
        {
            this.inventory.logic(controller);
        }

        // Update target entity...
        updateTargetEntity(controller);

        if (targetEntity != null)
        {
            float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);
            boolean withinAttackDistance = distance < attackDistance;

            // Determine if to rebuild path
            boolean rebuildPath;

            if (lastPathFound != null)
            {
                // Check if to recompute path if target entity has moved too far from end node
                rebuildPath = (lastPathFound.getTargetNodeDistance(targetEntity.positionNew) > lastPathFound.nodeSeparation);
            }
            else
            {
                rebuildPath = true;
            }

            // Switch current path to the new path
            if (rebuildPath)
            {
                // Re-compute path towards entity
                lastPathFound = controller.artificialIntelligenceManager.findPath(this, targetEntity);
                lastPathOffset = 0;
            }

            // Move along path
            if (lastPathFound != null && lastPathOffset < lastPathFound.getTotalNodes())
            {
                Node nextNode = lastPathFound.getNode(lastPathOffset);
                Vector2 nextNodeVector = nextNode.cachedVector;
                moveToTarget(nextNodeVector, withinAttackDistance);

                // Determine if we've met the current targeted node
                if (Vector2.distance(positionNew, nextNodeVector) < lastPathFound.nodeSeparation / 2.0f)
                {
                    lastPathOffset++;
                }
            }
            else
            {
                // Reset path, since it's now complete; rotate towards target
                lastPathFound = null;

                // Rotate towards target
                rotateToTarget(targetEntity.positionNew);
            }

            // Check distance between us and player, decide if to attack...
            if (withinAttackDistance)
            {
                // Fire selected weapon in inventory
                fireInventoryWeapon(controller);
            }
        }

        super.logic(controller);
    }

    private synchronized void rotateToTarget(Vector2 target)
    {
        // Get angle between current position and target
        float angleOffset = DefaultProximity.computeTargetAngleOffset(this, target);

        // Rotate towards target
        rotationOffset(angleOffset);
    }

    private synchronized void moveToTarget(Vector2 target, boolean rotateTowardsTarget)
    {
        // Get angle between current position and target
        float angleOffset = DefaultProximity.computeTargetAngleOffset(this, target);

        // Rotate towards target - either node or target
        if (rotateTowardsTarget)
        {
            // Rotate towards target
            rotateToTarget(targetEntity.positionNew);
        }
        else
        {
            // Rotate towards node or target
            rotationOffset(angleOffset);
        }

        // Move towards target
        positionOffset(
                Vector2.vectorFromAngle(rotation + angleOffset, followSpeed)
        );
    }

    private synchronized void resetTargetEntity()
    {
        targetEntity = null;
        lastPathFound = null;
    }

    private synchronized void updateTargetEntity(Controller controller)
    {
        if (targetEntity != null)
        {
            // Check target is still alive and not deleted
            if (targetEntity.isDead() || targetEntity.isDeleted())
            {
                resetTargetEntity();
            }
            else
            {
                // Check target is not too far away; we want to stay locked onto a target
                float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);

                if (distance > followDistance)
                {
                    resetTargetEntity();
                }
            }
        }

        if (targetEntity == null)
        {
            // Find nearest entity...
            // TODO: consider if we should test all vertices, expensive...
            List<ProximityResult> nearbyEnts = DefaultProximity.nearbyEnts(
                    controller, this, engageDistance, true, true
            );

            if (!nearbyEnts.isEmpty())
            {
                // Find nearest player
                Entity entity;
                for (ProximityResult proximityResult : nearbyEnts)
                {
                    entity = proximityResult.entity;

                    if  (
                            entity.faction != this.faction &&
                            (
                                (entity instanceof Player) || (entity instanceof AbstractVehicle) ||
                                (entity instanceof AbstractPedestrian)
                            )
                        )
                    {
                        targetEntity = entity;
                        break;
                    }
                }
            }
        }
    }

    private synchronized void fireInventoryWeapon(Controller controller)
    {
        // Don't continue without an inventory...
        if (inventory == null)
        {
            return;
        }

        // Compute offset to target
        float targetAngleOffsetToTarget = DefaultProximity.computeTargetAngleOffset(this, targetEntity.positionNew);

        if (inventory != null && Math.abs(targetAngleOffsetToTarget) <= ROTATIONAL_OFFSET_TO_ATTACK)
        {
            // Check we have line of sight
            CastingResult castingResult = Casting.cast(controller, this, rotation, attackDistance);

            if (castingResult.collision && (castingResult.victim instanceof EntityCastVictim))
            {
                // Add rotation noise, so player is less accurate
                if (attackRotationNoise > 0)
                {
                    float rotationNoise = ((float) Math.random() * (attackRotationNoise * 2.0f)) - attackRotationNoise;
                    rotationOffset(rotationNoise);
                }

                // Fire/use selected item
                inventory.selected.eventInvoke(controller, InventoryInvokeState.INVOKE_ONCE);
            }
        }
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }

    @Override
    public boolean isAi()
    {
        return true;
    }

}
