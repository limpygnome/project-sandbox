package com.projectsandbox.components.server.inventory.item.weapon;

import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;

/**
 *
 * @author limpygnome
 */
@InventoryItemTypeId(typeId = 100)
public class Gatling extends AbstractWeapon
{
    public static final long serialVersionUID = 1L;

    public Gatling()
    {
        super(
                (short) 1000,   // bullets per mag
                (short) 5,      // mags
                60,             // fire delay
                1500            // reload delay
        );

        this.invokeType = InventoryInvokeType.TOGGLE;
    }
}
