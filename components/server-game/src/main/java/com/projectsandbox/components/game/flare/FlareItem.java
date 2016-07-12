package com.projectsandbox.components.game.flare;

import com.projectsandbox.components.game.OwnershipComponent;
import com.projectsandbox.components.game.Player;
import com.projectsandbox.components.game.VelocityComponent;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.inventory.InventorySlotState;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractWeaponItem;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Enables a ship to shoot flares, which trigger a collision with entities. This is useful for things such as rockets,
 * since it will trigger them to explode.
 */
@InventoryItemTypeId(typeId = 806)
public class FlareItem extends AbstractWeaponItem
{
    public static final long serialVersionUID = 1L;

    private short flares;
    private long lifespan;
    private float maxVelocity;

    public FlareItem(short flares, long lifespan, float maxVelocity)
    {
        super(
                (short) 1,      // Bullets per mag
                flares,         // Mags
                1,              // Fire delay
                8000            // Reload delay
        );

        this.flares = flares;
        this.lifespan = lifespan;
        this.maxVelocity = maxVelocity;

        invokeType = InventoryInvokeType.FIRE_ONCE;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        // Fire flare
        fireFlare(controller);

        // Set slot as updated
        slot.setState(InventorySlotState.UPDATED);
    }

    private void fireFlare(Controller controller)
    {
        Entity parent = slot.inventory.parent;

        if (parent != null)
        {
            WorldMap map = parent.map;
            RespawnManager respawnManager = map.respawnManager;

            // Generate position behind entity
            Vector2 positionBehind = parent.positionNew.clone().offset(parent.rotation, (float) -parent.height / 2.0f);

            // Shoot flare behind ship
            Flare flare;
            for (int i = 0; i < flares; i++)
            {
                flare = new Flare(map, lifespan);
                fireFlare(controller, respawnManager, flare, parent, positionBehind);
            }
        }
    }

    private void fireFlare(Controller controller, RespawnManager respawnManager, Flare flare, Entity parent, Vector2 positionBehind)
    {
        // Generate random velocity to flare
        float x = ( (float) Math.random() * maxVelocity * 2.0f) - maxVelocity;
        float y = ( (float) Math.random() * maxVelocity * 2.0f) - maxVelocity;

        // Add velocity component
        VelocityComponent velocityComponent = new VelocityComponent(1);
        velocityComponent.setInitialVelocity(new Vector2(x, y));
        flare.components.add(velocityComponent);

        // Add ownership component
        OwnershipComponent ownershipComponent = new OwnershipComponent(parent.getPlayer());
        flare.components.add(ownershipComponent);

        respawnManager.respawn(new PositionPendingRespawn(
                controller, flare, positionBehind.x, positionBehind.y, 0.0f
        ));
    }

}
