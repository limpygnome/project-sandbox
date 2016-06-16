package com.projectsandbox.components.game.inventory.item;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;

/**
 * Enables a ship to shoot flares, which trigger a collision with entities. This is useful for things such as rockets,
 * since it will trigger them to explode.
 */
@InventoryItemTypeId(typeId = 804)
public class FlaresItem extends AbstractInventoryItem
{

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return null;
    }

}
