package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeType;

/**
 *
 * @author limpygnome
 */
@InventyoryItemTypeId(typeId = 100)
public class Smg extends AbstractWeapon
{
    public Smg()
    {
        super(
                (short) 60,
                (short) 5,
                120,
                1500
        );

        this.invokeType = InventoryInvokeType.FIRE_ONCE;
    }
}
