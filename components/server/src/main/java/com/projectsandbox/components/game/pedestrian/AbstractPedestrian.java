package com.projectsandbox.components.game.pedestrian;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.entity.ai.IdleMode;
import com.projectsandbox.components.server.entity.ai.PedestrianState;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.casting.Casting;
import com.projectsandbox.components.server.entity.physics.casting.CastingResult;
import com.projectsandbox.components.server.entity.physics.casting.victims.EntityCastVictim;
import com.projectsandbox.components.server.entity.ai.pathfinding.Node;
import com.projectsandbox.components.server.entity.ai.pathfinding.Path;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;
import com.projectsandbox.components.server.constant.entity.PedestrianConstants;

import java.util.Set;

/**
 * Used as a base class for implementing pedestrians, which behave similar to players.
 */
public abstract class AbstractPedestrian extends Entity
{
    /**
     * Maximum steps to compute for every idle walk path.
     *
     * The higher this value, the less chaotic, due to a decreased chance of re-walking the same nodes.
     */
    public static final int IDLE_WALK_MAX_STEPS = 32;

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

    /* Cached calculation to indicate if this pedestrian can even attack. */
    private boolean flagCanAttack;

    public AbstractPedestrian(WorldMap map, short width, short height, float health, Class[] inventoryItems,
                              float engageDistance,  float followSpeed, float followDistance, float attackDistance,
                              float attackRotationNoise, IdleMode idleMode)
    {
        super(map, width, height);

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

        this.flagCanAttack = (attackDistance > 0.0f);

        this.state = PedestrianState.Idle;
    }

    @Override
    public synchronized void eventReset(Controller controller, Spawn spawn, boolean respawnAfterPersisted)
    {
        super.eventReset(controller, spawn, respawnAfterPersisted);

        // Save spawn if idle mode is to return to spawn, so we can later walk back to it...
        if (idleMode == IdleMode.RETURN_TO_SPAWN)
        {
            this.lastSpawn = spawn;
        }

        // Reset state/target etc
        resetTarget();
    }

    @Override
    public synchronized void eventLogic(Controller controller)
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

        super.eventLogic(controller);
    }

    private synchronized void trackingLogic(Controller controller)
    {
        if (state != PedestrianState.Idle)
        {
            float distance = Vector2.distance(this.positionNew, targetVector);
            boolean withinAttackDistance = (flagCanAttack && distance < attackDistance);

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
                    lastPathFound = map.artificialIntelligenceManager.findPath(this, targetEntity);
                    lastPathOffset = 0;
                }
            }

            // Move along path...
            if (lastPathFound != null && lastPathOffset < lastPathFound.getTotalNodes())
            {
                Node nextNode = lastPathFound.getNode(lastPathOffset);
                Vector2 nextNodeVector = nextNode.cachedVector;
                moveToTarget(nextNodeVector, withinAttackDistance);

                // Determine if we've met the current targeted node, before we move onto the next...
                float distanceToTarget = Vector2.distance(positionNew, nextNodeVector);

                if (distanceToTarget < lastPathFound.nodeSeparation / 2.0f)
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
                        moveToTarget(targetVector, true);

                        if (positionNew.x == lastSpawn.x && positionNew.y == lastSpawn.y)
                        {
                            // We've reached the spawn; rotate as we do when spawning and reset target...
                            rotation(lastSpawn.rotation);
                            resetTarget();
                        }
                        break;
                    case IdleWalk:
                        // Set state to idle, this will trigger idle walk when updating target...
                        state = PedestrianState.Idle;
                        break;
                    default:
                        state = PedestrianState.Idle;
                        break;
                }
            }

            // Check distance between us and player, decide if to attack...
            if (withinAttackDistance && state == PedestrianState.TrackingEntity)
            {
                // Fire selected weapon in inventory
                fireInventoryWeapon(controller);
            }
        }
    }

    private synchronized void rotateToTarget(Vector2 target)
    {
        // Get angle between current position and target
        float angleOffset = Vector2.computeTargetAngleOffset(this, target);

        // Rotate towards target
        rotationOffset(angleOffset);
    }

    private synchronized void moveToTarget(Vector2 target, boolean rotateTowardsTarget)
    {
        // Get angle between current position and target
        float angleOffset = Vector2.computeTargetAngleOffset(this, target);

        // Measure distance to target
        float distance = Vector2.distance(positionNew, target);

        // If distance is less than speed we can move, we'll round it down to just the required distance, else
        // we might indefinitely overshoot the target
        if (distance < followSpeed)
        {
            position(target);
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
            rotateToTarget(target);
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
            if (targetEntity.isDeleted())
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
        if (flagCanAttack && state.IDLE)
        {
            // Fetch quad-tree
            QuadTree quadTree = map.entityManager.getQuadTree();

            // Find nearest entity...
            Set<ProximityResult> nearbyEnts = quadTree.getEntitiesWithinRadius(this, engageDistance);

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
                                (entity instanceof PlayerEntity)            ||
                                (entity instanceof AbstractPedestrian)
                            )
                        )
                    {
                        // Reset pre-existing target
                        resetTarget();

                        // Set found entity as the target
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
                    lastPathFound = map.artificialIntelligenceManager.findPath(this, targetVector);
                    lastPathOffset = 0;

                    state = PedestrianState.IdleReturnToSpawn;
                    break;
                case WALK:
                    // Rebuild idle path
                    lastPathFound = map.artificialIntelligenceManager.findIdlePath(this, IDLE_WALK_MAX_STEPS);

                    if (lastPathFound != null && lastPathFound.getTotalNodes() > 0)
                    {
                        lastPathOffset = 0;
                        targetVector = lastPathFound.getTargetVector();
                        state = PedestrianState.IdleWalk;
                    }
                    break;
            }
        }
    }

    private synchronized void resetTarget()
    {
        targetEntity = null;
        targetVector = null;
        lastPathFound = null;
        lastPathOffset = 0;

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
        float targetAngleOffsetToTarget = Vector2.computeTargetAngleOffset(this, targetVector);

        if (inventory != null && Math.abs(targetAngleOffsetToTarget) <= PedestrianConstants.ROTATIONAL_OFFSET_TO_ATTACK)
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
