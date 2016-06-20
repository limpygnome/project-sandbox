package com.projectsandbox.components.game.flare;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractWeaponItem;

/**
 * Enables a ship to shoot flares, which trigger a collision with entities. This is useful for things such as rockets,
 * since it will trigger them to explode.
 */
@InventoryItemTypeId(typeId = 804)
public class FlareItem extends AbstractWeaponItem
{
    public static final long serialVersionUID = 1L;

    public FlareItem(short flares)
    {
        super(
                (short) 1,      // Bullets per mag
                flares,         // Mags
                1,              // Fire delay
                8000            // Reload delay
        );

        invokeType = InventoryInvokeType.FIRE_ONCE;
    }

    @Override
    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        switch (invokeState)
        {
            case INVOKE_ONCE:
                fire();
                break;
        }
    }

    private void fire()
    {
        // Shoot flares in random direction behind ship
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return null;
    }

}
