package com.projectsandbox.components.game.inventory.item.weapon;

import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;

/**
 *
 * @author limpygnome
 */
@InventoryItemTypeId(typeId = 100)
public class GatlingItem extends AbstractWeaponItem
{
    public static final long serialVersionUID = 1L;

    public GatlingItem()
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
