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
import com.limpygnome.projectsandbox.server.world.Spawn;

import java.util.List;

import static com.limpygnome.projectsandbox.server.constant.entity.PedestrianConstants.*;

/**
 * Used as a base class for implementing pedestrians, which behave similar to players.
 */
public abstract class AbstractPedestrian extends Entity
{
    /**
     * Specifies what the pedestrian should do when idle.
     */
    public enum IdleMode
    {
        /**
         * Causes the pedestrian to walk around the world.
         */
        WALK,

        /**
         * Causes the pedestrian to return to their spawn position.
         */
        RETURN_TO_SPAWN
    }

    /**
     * Used to track the current state of the pedestrian.
     */
    public enum PedestrianState
    {
        /**
         * Indicates the pedestrian is idle and returning to its spawn position.
         */
        IdleReturnToSpawn,

        /**
         * Indicates the pedestrian is idle and just aimlessly walking around the world.
         */
        IdleWalk,

        /**
         * Indicates the pedestrian is idle.
         */
        Idle,

        /**
         * Indicates the pedestrian is in pursuit and attacking an entity.
         */
        TrackingEntity
    }

    private Inventory inventory;
    private float engageDistance;

    private float followSpeed;
    private float followDistance;
    private float attackDistance;
    private float attackRotationNoise;
    private IdleMode idleMode;

    private Entity targetEntity;
    private Vector2 targetVector;
    private Spawn lastSpawn;

    private Path lastPathFound;
    private int lastPathOffset;
    private PedestrianState state;

    public AbstractPedestrian(short width, short height, float health, Class[] inventoryItems, float engageDistance,
                              float followSpeed, float followDistance, float attackDistance, float attackRotationNoise,
                              IdleMode idleMode)
    {
        super(width, height);

        setMaxHealth(health);

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
        this.idleMode = idleMode;

        this.targetEntity = null;
        this.targetVector = null;
        this.lastSpawn = null;

        this.state = PedestrianState.Idle;
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        // Save spawn if idle mode is to return to spawn, so we can later walk back to it...
        if (idleMode == IdleMode.RETURN_TO_SPAWN)
        {
            this.lastSpawn = spawn;
        }

        super.eventSpawn(controller, spawn);
    }

    @Override
    public synchronized void logic(Controller controller)
    {
        // Run inventory logic
        if (this.inventory != null)
        {
            this.inventory.logic(controller);
        }

        // Update target...
        updateTarget(controller);

        // Execute tracking logic to search and destroy...or walk around...
        trackingLogic(controller);

        super.logic(controller);
    }

    private synchronized void trackingLogic(Controller controller)
    {
        if (state != PedestrianState.Idle)
        {
            float distance = Vector2.distance(this.positionNew, targetVector);
            boolean withinAttackDistance = (distance < attackDistance);

            // Determine if to rebuild path due to target moving too far away from pre-computed destination
            // -- Only applies when tracking an entity
            if (state == PedestrianState.TrackingEntity)
            {
                boolean rebuildPath;

                if (lastPathFound != null)
                {
                    // Check if to recompute path if target entity has moved too far from end node
                    rebuildPath = (lastPathFound.getTargetNodeDistance(targetVector) > lastPathFound.nodeSeparation);
                }
                else
                {
                    rebuildPath = true;
                }

                // Rebuild path if required...
                if (rebuildPath)
                {
                    // Re-compute path towards entity
                    lastPathFound = controller.artificialIntelligenceManager.findPath(this, targetEntity);
                    lastPathOffset = 0;
                }
            }

            // Move along path...
            if (lastPathFound != null && lastPathOffset < lastPathFound.getTotalNodes())
            {
                Node nextNode = lastPathFound.getNode(lastPathOffset);
                Vector2 nextNodeVector = nextNode.cachedVector;
                moveToTarget(nextNodeVector, distance, withinAttackDistance);

                // Determine if we've met the current targeted node, before we move onto the next...
                if (Vector2.distance(positionNew, nextNodeVector) < lastPathFound.nodeSeparation / 2.0f)
                {
                    // Move onto the next node...
                    lastPathOffset++;
                }
            }
            else
            {
                // We've finished moving along the path; use state to determine what to do next...
                switch (state)
                {
                    case TrackingEntity:
                        // Rotate towards target
                        rotateToTarget(targetVector);
                        break;
                    case IdleReturnToSpawn:
                        // Continue moving towards spawn until position is exact...
                        moveToTarget(targetVector, distance, true);

                        if (positionNew.x == lastSpawn.x && positionNew.y == lastSpawn.y)
                        {
                            // We've reached the spawn; rotate as we do when spawning and reset target...
                            rotation(lastSpawn.rotation);
                            resetTarget();
                        }
                        break;
                    case IdleWalk:
                        // Update path to walk elsewhere...

                        break;
                    default:
                        state = PedestrianState.Idle;
                        break;
                }
            }

            // Check distance between us and player, decide if to attack...
            if (state == PedestrianState.TrackingEntity && withinAttackDistance)
            {
                // Fire selected weapon in inventory
                fireInventoryWeapon(controller);
            }
        }
    }

    private synchronized void rotateToTarget(Vector2 target)
    {
        // Get angle between current position and target
        float angleOffset = DefaultProximity.computeTargetAngleOffset(this, target);

        // Rotate towards target
        rotationOffset(angleOffset);
    }

    private synchronized void moveToTarget(Vector2 target, float distance, boolean rotateTowardsTarget)
    {
        // Get angle between current position and target
        float angleOffset = DefaultProximity.computeTargetAngleOffset(this, target);

        // If distance is less than speed we can move, we'll round it down to just the required distance, else
        // we might indefinitely overshoot the target
        if (distance < followSpeed)
        {
            position(targetVector);
        }
        else
        {
            // Move towards target
            positionOffset(
                    Vector2.vectorFromAngle(rotation + angleOffset, followSpeed)
            );
        }

        // Rotate towards target - either node or target
        if (rotateTowardsTarget)
        {
            // Rotate towards target
            rotateToTarget(targetVector);
        }
        else
        {
            // Rotate towards node or target
            rotationOffset(angleOffset);
        }
    }

    private synchronized void updateTarget(Controller controller)
    {
        // Check if current target is still valid and update target vector
        if (state == PedestrianState.TrackingEntity)
        {
            // Check target is still alive and not deleted
            if (targetEntity.isDead() || targetEntity.isDeleted())
            {
                resetTarget();
            }
            else
            {
                // Check target is not too far away; we want to stay locked onto a target
                float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);

                if (distance > followDistance)
                {
                    resetTarget();
                }
                else
                {
                    // Update target vector
                    targetVector = targetEntity.positionNew;
                }
            }
        }

        // See if we can find a new target...
        if (state == PedestrianState.Idle)
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
                        targetVector = targetEntity.positionNew;
                        state = PedestrianState.TrackingEntity;
                        break;
                    }
                }
            }
        }

        // If no target still found, and not already moving to spawn or walking around, enact idle mode...
        if (state == PedestrianState.Idle)
        {
            switch (idleMode)
            {
                case RETURN_TO_SPAWN:
                    // Update target to spawn point
                    targetVector = new Vector2(lastSpawn.x, lastSpawn.y);

                    // Rebuild path
                    lastPathFound = controller.artificialIntelligenceManager.findPath(this, targetVector);

                    state = PedestrianState.IdleReturnToSpawn;
                    break;
                case WALK:

                    state = PedestrianState.IdleWalk;
                    break;
            }
        }
    }

    private synchronized void resetTarget()
    {
        targetEntity = null;
        targetVector = null;
        lastPathFound = null;

        state = PedestrianState.Idle;
    }

    private synchronized void fireInventoryWeapon(Controller controller)
    {
        // Don't continue without an inventory...
        if (inventory == null)
        {
            return;
        }

        // Compute offset to target
        float targetAngleOffsetToTarget = DefaultProximity.computeTargetAngleOffset(this, targetVector);

        if (inventory != null && Math.abs(targetAngleOffsetToTarget) <= ROTATIONAL_OFFSET_TO_ATTACK)
        {
            // Check we have line of sight
            CastingResult castingResult = Casting.cast(controller, this, rotation, attackDistance);

            if (castingResult.collision && (castingResult.victim instanceof EntityCastVictim))
            {
                EntityCastVictim entityCastVictim = (EntityCastVictim) castingResult.victim;

                // Check we will not hit another pedestrian, unless different faction...
                if (
                        entityCastVictim.entity != null &&
                        (
                            !(entityCastVictim.entity instanceof AbstractPedestrian) || entityCastVictim.entity.faction != faction)
                        )
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

    public PedestrianState getPedestrianState()
    {
        return state;
    }

}
