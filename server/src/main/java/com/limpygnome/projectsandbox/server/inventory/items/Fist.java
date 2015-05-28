package com.limpygnome.projectsandbox.server.inventory.items;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;

/**
 * Created by limpygnome on 04/05/15.
 */
@InventyoryItemTypeId(typeId = 1)
public class Fist extends AbstractInventoryItem
{
    @Override
    public String eventFetchItemText(Controller controller)
    {
        return "";
    }
}
