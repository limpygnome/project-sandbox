package com.limpygnome.projectsandbox.server.inventory.item.weapon;

import com.limpygnome.projectsandbox.server.inventory.annotation.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.InventoryInvokeType;

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
