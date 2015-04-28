package com.limpygnome.projectsandbox.server.inventory;

/**
 * All of the data regarding an item being held in an inventory, as a slot.
 *
 * @author limpygnome
 */
public class InventorySlotData
{
    public int id;
    public Inventory inventory;
    public InventoryItemState state;

    public InventorySlotData(int id, Inventory inventory)
    {
        this.id = id;
        this.inventory = inventory;
        this.state = InventoryItemState.CREATED;
    }
}
