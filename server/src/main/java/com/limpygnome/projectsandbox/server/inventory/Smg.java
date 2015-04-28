package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;

/**
 *
 * @author limpygnome
 */
@InventyoryItemTypeId(typeId = 100)
public class Smg extends Weapon
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
