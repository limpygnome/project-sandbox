package com.projectsandbox.components.game.inventory.item;

import com.projectsandbox.components.game.component.shield.ShieldComponent;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.inventory.InventorySlotState;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;

/**
 * An item which creates a protective shield around a ship.
 */
@InventoryItemTypeId(typeId = 802)
public class ShieldItem extends AbstractInventoryItem
{
    public static final long serialVersionUID = 1L;

    // Settings
    private float maxHealth;    // The maximum health of the shield
    private float regenStep;    // The amount the shield regenerates each logic cycle

    public ShieldItem(float maxHealth, float regenStep)
    {
        this.maxHealth = maxHealth;
        this.regenStep = regenStep;

        // Setup type to be toggled
        this.invokeType = InventoryInvokeType.TOGGLE;
    }

    @Override
    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        Entity parent = slot.inventory.parent;

        switch (slot.invokeState)
        {
            // Shield on...
            case ON:
                // Remove shield component from parent if present
                if (parent.components.remove(ShieldComponent.class) == null)
                {
                    // No shield present, thus add it...
                    parent.components.add(new ShieldComponent(parent, maxHealth, regenStep));
                }
                break;
        }

        slot.setState(InventorySlotState.CHANGED);
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        Entity parent = slot.inventory.parent;
        ShieldComponent shield = (ShieldComponent) parent.components.fetchComponent(ShieldComponent.class);

        String text;
        if (shield != null)
        {
            float healthPercent = (shield.getHealth() / shield.getMaxHealth()) * 100.0f;
            text = String.format("%.0f%%", healthPercent);
        }
        else
        {
            text = "offline";
        }

        return text;
    }

}
