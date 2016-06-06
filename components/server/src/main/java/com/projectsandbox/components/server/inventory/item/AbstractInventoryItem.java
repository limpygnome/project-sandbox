package com.projectsandbox.components.server.inventory.item;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.InventorySlotData;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.InventoryInvokeType;
import com.projectsandbox.components.server.inventory.InventoryMergeResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 *
 * @author limpygnome
 */
@InventoryItemTypeId(typeId = 0)
public abstract class AbstractInventoryItem implements Serializable
{
    private final static Logger LOG = LogManager.getLogger(AbstractInventoryItem.class);

    public static final long serialVersionUID = 1L;

    public short typeId = 0;

    public InventorySlotData slot;
    public InventoryInvokeType invokeType;
    
    public AbstractInventoryItem()
    {
        this.slot = null;
        this.invokeType = InventoryInvokeType.FIRE_ONCE;

        // Read type from annotation
        Annotation annotation = getClass().getAnnotation(InventoryItemTypeId.class);
        InventoryItemTypeId inventoryType = (InventoryItemTypeId) annotation;
        this.typeId = inventoryType.typeId();
    }

    public void logic(Controller controller)
    {
        switch (invokeType)
        {
            case TOGGLE:
                if (!slot.keyAlreadyDown && slot.keyDown)
                {
                    slot.keyAlreadyDown = true;
                    slot.invokeState = InventoryInvokeState.ON;

                    eventInvoke(controller, InventoryInvokeState.ON);
                }
                else if (slot.keyAlreadyDown && !slot.keyDown)
                {
                    slot.keyAlreadyDown = false;
                    slot.invokeState = InventoryInvokeState.OFF;

                    eventInvoke(controller, InventoryInvokeState.OFF);
                }
                break;
            case FIRE_ONCE:
                if (!slot.keyAlreadyDown && slot.keyDown)
                {
                    slot.keyAlreadyDown = true;
                    eventInvoke(controller, InventoryInvokeState.INVOKE_ONCE);
                }
                else if (slot.keyAlreadyDown && !slot.keyDown)
                {
                    slot.keyAlreadyDown = false;
                }
                break;
            default:
                throw new IllegalArgumentException("Unhandled invoke type");
        }
    }

    /**
     * Used to merge similar inventory items.
     * 
     * By default, this will not allow an item to be added.
     * 
     * @param item The item being added to the inventory.
     * @return The result from the merge process.
     */
    public InventoryMergeResult merge(AbstractInventoryItem item)
    {
        return InventoryMergeResult.DONT_ADD;
    }

    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        // Nothing by default...
    }

    /**
     * When an item has an updated state, the text is re-sent to the client. This should display the state or/and ammo,
     * although only a limited amount of chars can be sent.
     *
     * @param controller
     * @return
     */
    public abstract String eventFetchItemText(Controller controller);
}
