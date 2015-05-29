package com.limpygnome.projectsandbox.server.inventory.items;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventoryItemTypeId;

/**
 * Created by limpygnome on 04/05/15.
 */
@InventoryItemTypeId(typeId = 1)
public class Fist extends AbstractInventoryItem
{
    @Override
    public String eventFetchItemText(Controller controller)
    {
        return "";
    }
}
