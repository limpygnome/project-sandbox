package com.limpygnome.projectsandbox.website.inventory;

import com.limpygnome.projectsandbox.website.inventory.annotations.InventyoryItemTypeId;
import com.limpygnome.projectsandbox.website.inventory.annotations.InventyoryItemTypeId;

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
