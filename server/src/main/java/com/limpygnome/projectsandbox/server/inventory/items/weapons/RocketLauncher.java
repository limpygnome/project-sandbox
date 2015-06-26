package com.limpygnome.projectsandbox.server.inventory.items.weapons;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.Vector2;
import com.limpygnome.projectsandbox.server.ents.types.weapons.RPG;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeType;

/**
 * Created by limpygnome on 26/06/15.
 */
@InventoryItemTypeId(typeId = 601)
public class RocketLauncher extends AbstractWeapon
{
    public static final long serialVersionUID = 1L;

    public RocketLauncher()
    {
        super(
                (short) 1,  // bullets per mag
                (short) 10, // mags
                1,          // fire delay
                500         // reload delay
        );

        this.invokeType = InventoryInvokeType.TOGGLE;
    }

    @Override
    protected void fireBullet(Controller controller)
    {
        Entity owner = this.slot.inventory.parent;

        if (owner != null)
        {
            // Compute position in front of player for RPG
            Vector2 position = owner.positionNew;
            Vector2 positionOffset = Vector2.vectorFromAngle(owner.rotation, );
            position.offset();

            // Create rocket entity
            controller.entityManager.add(new RPG());
        }
    }

}
