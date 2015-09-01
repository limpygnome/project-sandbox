package com.limpygnome.projectsandbox.server.entity.imp.living;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.ai.ComputedPath;
import com.limpygnome.projectsandbox.server.entity.annotation.EntityType;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.casting.Casting;
import com.limpygnome.projectsandbox.server.entity.physics.casting.CastingResult;
import com.limpygnome.projectsandbox.server.entity.physics.pathfinding.Path;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.DefaultProximity;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.ProximityResult;
import com.limpygnome.projectsandbox.server.entity.physics.proximity.RotateResult;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.Smg;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.util.CustomMath;

import java.util.List;

/**
 * Looks and acts as a player, but using AI.
 */
@EntityType(typeId = 510, typeName = "living/pedestrian")
public class Pedestrian extends Entity
{
    private Entity targetEntity;
    private Inventory inventory;

    private float followDistance;
    private float attackDistance;
    private float attackRotationOffset;

    public Pedestrian()
    {
        super((short) 16, (short) 9);

        setMaxHealth(80.0f);

        this.followDistance = 450.0f;
        this.attackDistance = 100.0f;
        this.attackRotationOffset = 0.26f;

        this.inventory = new Inventory(this);
        this.inventory.add(new Class[]{
                Smg.class
        });
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
            // Re-compute path towards entity
            Path computedPath = controller.artificialIntelligenceManager.findPath(this, targetEntity);

            // Move towards target tile/node

            // Rotate towards target tile/node, or entity
            RotateResult rotateResult;

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

    private synchronized void updateTargetEntity(Controller controller)
    {
        if (targetEntity != null)
        {
            // Check target is not too far away; we want to stay locked onto a target
            float distance = Vector2.distance(this.positionNew, targetEntity.positionNew);

            if (distance > followDistance)
            {
                targetEntity = null;
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
                targetEntity = nearbyEnts.get(0).entity;
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
