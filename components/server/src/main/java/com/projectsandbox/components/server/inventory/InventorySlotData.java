package com.projectsandbox.components.server.inventory;

import java.io.Serializable;

/**
 * All of the data regarding an item being held in an inventory, as a slot.
 */
public class InventorySlotData implements Serializable
{
    public static final long serialVersionUID = 1L;

    public short id;
    public byte idByte;
    public Inventory inventory;
    public InventorySlotState slotState;
    public InventoryInvokeState invokeState;

    public transient boolean keyDown;
    public transient boolean keyAlreadyDown;

    public InventorySlotData(short id, Inventory inventory)
    {
        this.id = id;
        this.idByte = (byte) id;
        this.inventory = inventory;
        this.slotState = InventorySlotState.CREATED;
        this.invokeState = InventoryInvokeState.OFF;

        this.keyDown = false;
        this.keyAlreadyDown = false;
    }

    public void setState(InventorySlotState slotState)
    {
        // Check if we're allowed to update the state
        boolean allowUpdate;
        switch (slotState)
        {
            case CREATED:
                allowUpdate = false;
                break;
            case PENDING_REMOVE:
            case REMOVED:
                allowUpdate = true;
                break;
            case UPDATED:
                allowUpdate = this.slotState != InventorySlotState.PENDING_REMOVE && this.slotState != InventorySlotState.REMOVED;
                break;
            case NONE:
                allowUpdate = false;
                break;
            default:
                throw new RuntimeException("Unhandled inventory state");
        }

        // Update the state
        if (allowUpdate)
        {
            this.slotState = slotState;
        }
    }
}
