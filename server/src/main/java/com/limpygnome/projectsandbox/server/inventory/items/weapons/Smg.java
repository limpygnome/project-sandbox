package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;

/**
 *
 * @author limpygnome
 */
@InventyoryItemTypeId(typeId = 100)
public class Smg extends AbstractWeapon
{
    public Smg(Inventory inventory)
    {
        super(
                inventory,
                (short) 60,
                (short) 5,
                120,
                1500
        );
    }
}
