package com.limpygnome.projectsandbox.server.entity.imp.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Node;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.Smg;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.limpygnome.projectsandbox.server.constant.PlayerConstants.DEFAULT_MOVEMENT_SPEED_FACTOR;

/**
 * Looks and acts as a player, but using AI.
 */
@EntityType(typeId = 510, typeName = "living/pedestrian")
public class Pedestrian extends Entity
{
    private final static Logger LOG = LogManager.getLogger(Pedestrian.class);

    private Entity targetEntity;
    private Inventory inventory;

    private float followDistance;
    private float attackDistance;
    private float attackRotationOffset;

    private Path lastPathFound;
    private int lastPathOffset;

    public Pedestrian()
    {
        super((short) 16, (short) 9);

        setMaxHealth(80.0f);

        this.targetEntity = null;

        this.inventory = new Inventory(this);
        this.inventory.add(new Class[]{
                Smg.class
        });

        this.followDistance = 450.0f;
        this.attackDistance = 100.0f;
        this.attackRotationOffset = 0.26f;
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

                LOG.debug("Computed new path - {} nodes", lastPathFound.getTotalNodes());
            }

            // Move along path
            if (lastPathFound != null && lastPathOffset < lastPathFound.getTotalNodes())
            {
                Node nextNode = lastPathFound.getNode(lastPathOffset);
                Vector2 nextNodeVector = nextNode.cachedVector;
                moveToTarget(nextNodeVector);

                // Determine if we've met the node
                if (Vector2.distance(positionNew, nextNodeVector) < lastPathFound.nodeSeparation / 2.0f)
                {
                    lastPathOffset++;
                    LOG.info("Node {} / {} reached", lastPathOffset, lastPathFound.getTotalNodes());
                }
            }
            else
            {
                // Reset path, since it's now complete; rotate towards target
                lastPathFound = null;

                rotateToTarget(targetEntity.positionNew);
                LOG.debug("Path complete");
            }

            // Check distance between us and player, decide if to attack...
            float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);

            if (distance < attackDistance)
            {
                // Fire selected weapon in inventory
                fireInventoryWeapon(controller);
            }
        }

        super.logic(controller);
    }

    private synchronized float rotateToTarget(Vector2 target)
    {
        // Get angle between current position and target
        float angleOffset = DefaultProximity.computeTargetAngleOffset(this, target);

        // Rotate towards target
        rotationOffset(angleOffset);

        return angleOffset;
    }

    private synchronized void moveToTarget(Vector2 target)
    {
        float angleOffset = rotateToTarget(target);

        // Move towards target
        positionOffset(
                Vector2.vectorFromAngle(rotation + angleOffset, DEFAULT_MOVEMENT_SPEED_FACTOR / 2.0f)//DEFAULT_MOVEMENT_SPEED_FACTOR)
        );

        LOG.debug("Moving to target - vector: {}, angle offset: {}", target, angleOffset);
    }

    private synchronized void updateTargetEntity(Controller controller)
    {
        if (targetEntity != null)
        {
            // Check target is not too far away; we want to stay locked onto a target
            float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);

            if (distance > followDistance)
            {
                targetEntity = null;
                lastPathFound = null;
                LOG.debug("Target entity too far, reset");
            }
        }

        if (targetEntity == null)
        {
            // Find nearest entity...
            // TODO: consider if we should test all vertices, expensive...
            List<ProximityResult> nearbyEnts = DefaultProximity.nearbyEnts(
                    controller, this, followDistance, true, true
            );

            if (!nearbyEnts.isEmpty())
            {
                // Find nearest player
                Entity entity;
                for (ProximityResult proximityResult : nearbyEnts)
                {
                    entity = proximityResult.entity;

                    if (entity instanceof Player)
                    {
                        targetEntity = entity;
//                        LOG.debug("New target entity - entity: {}", targetEntity);
                        break;
                    }
                }
            }
        }
    }

    private synchronized void fireInventoryWeapon(Controller controller)
    {
        // Don't continue without an inventory...
        if (this.inventory == null)
        {
            return;
        }

        // Compute offset to target
        float targetAngleOffsetToTarget = DefaultProximity.computeTargetAngleOffset(this, targetEntity.positionNew);

        if (this.inventory != null && Math.abs(targetAngleOffsetToTarget) <= attackRotationOffset)
        {
            // Check selected item has ammo / usable, else remove it

            // Fire/use selected item
        }
    }

    @Override
    public String friendlyName()
    {
        return "Pedestrian";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return null;
    }

}
