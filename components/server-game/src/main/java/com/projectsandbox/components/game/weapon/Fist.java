package com.projectsandbox.components.game.weapon;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;

/**
 * Created by limpygnome on 04/05/15.
 */
@InventoryItemTypeId(typeId = 1)
public class Fist extends AbstractInventoryItem
{
    public static final long serialVersionUID = 1L;

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return "";
    }

}
