package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeState;
import com.limpygnome.projectsandbox.server.inventory.enums.InventorySlotState;

/**
 * All of the data regarding an item being held in an inventory, as a slot.
 *
 * @author limpygnome
 */
public class InventorySlotData
{
    public short id;
    public byte idByte;
    public Inventory inventory;
    public InventorySlotState slotState;
    public InventoryInvokeState invokeState;

    public InventorySlotData(short id, Inventory inventory)
    {
        this.id = id;
        this.idByte = (byte) id;
        this.inventory = inventory;
        this.slotState = InventorySlotState.CREATED;
        this.invokeState = InventoryInvokeState.OFF;
    }
}
