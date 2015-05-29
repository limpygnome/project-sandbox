package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.inventory.annotations.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeType;

/**
 *
 * @author limpygnome
 */
@InventoryItemTypeId(typeId = 100)
public class Smg extends AbstractWeapon
{
    public Smg()
    {
        super(
                (short) 60, // bullets per mag
                (short) 5,  // mags
                60,         // fire delay
                1500        // reload delay
        );

        this.invokeType = InventoryInvokeType.TOGGLE;
    }
}
