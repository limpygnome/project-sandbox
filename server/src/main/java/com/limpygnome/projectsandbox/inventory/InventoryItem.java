package com.limpygnome.projectsandbox.inventory;

import com.limpygnome.projectsandbox.inventory.annotations.InventyoryItemTypeId;
import java.io.Serializable;

/**
 *
 * @author limpygnome
 */
@InventyoryItemTypeId(typeId = 0)
public abstract class InventoryItem implements Serializable
{
    public static final long serialUID = 1L;
    
    public Inventory inventory;
    
    public InventoryItem(Inventory inventory)
    {
        this.inventory = inventory;
    }
    
    /**
     * Used to merge similar inventory items.
     * 
     * By default, this will not allow an item to be added.
     * 
     * @param item The item being added to the inventory.
     * @return The result from the merge process.
     */
    public InventoryMergeResult merge(InventoryItem item)
    {
        return InventoryMergeResult.DONT_ADD;
    }
}
