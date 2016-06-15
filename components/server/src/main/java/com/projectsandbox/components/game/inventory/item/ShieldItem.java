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
    public static final long serialVersionUID = 2L;
    private ShieldComponent component;

    public ShieldItem(float maxHealth, float regenStep, long rechargeDelay, float sizeMultiplier)
    {
        // Create component
        component = new ShieldComponent(this, maxHealth, regenStep, rechargeDelay, sizeMultiplier);

        // Setup type to be toggled
        this.invokeType = InventoryInvokeType.TOGGLE;
    }

    @Override
    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        Entity parent = slot.inventory.parent;

        switch (slot.invokeState)
        {
            // Toggle force-field...
            case ON:
                // Remove shield component from parent if present
                if (parent.components.remove(ShieldComponent.class) == null)
                {
                    // No force-field at present, thus add it...
                    parent.components.add(component);
                    component.update(parent);
                }
                break;
        }

        slot.setState(InventorySlotState.UPDATED);
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        Entity parent = slot.inventory.parent;
        ShieldComponent shield = (ShieldComponent) parent.components.fetchComponent(ShieldComponent.class);

        String text;
        if (shield != null)
        {
            float shieldHealth = shield.getHealth();

            if (shieldHealth == 0.0f)
            {
                text = "depleted";
            }
            else
            {
                float healthPercent = (shield.getHealth() / shield.getMaxHealth()) * 100.0f;
                text = String.format("%.0f%%", healthPercent);
            }
        }
        else
        {
            text = "offline";
        }

        return text;
    }

}
