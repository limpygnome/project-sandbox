package com.limpygnome.projectsandbox.inventory;

import com.limpygnome.projectsandbox.inventory.annotations.InventyoryItemTypeId;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
@InventyoryItemTypeId(typeId = 0)
public abstract class InventoryItem implements Serializable
{
    public static final long serialVersionUID = 1L;
    
    static short typeId = 0;
    
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
    
    public void writeInventoryCreatePacket(LinkedList<Object> packetData)
    {
        // Nothing by default...
    }
    
    public void writeInventoryUpdatePacket(LinkedList<Object> packetData)
    {
        // Nothing by default...
    }
    
    public final short getTypeId()
    {
        return typeId;
    }
}
