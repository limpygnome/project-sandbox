package com.projectsandbox.components.server.inventory.item.weapon;

import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;

/**
 *
 * @author limpygnome
 */
@InventoryItemTypeId(typeId = 100)
public class Smg extends AbstractWeapon
{
    public static final long serialVersionUID = 1L;

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
