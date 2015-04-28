package com.limpygnome.projectsandbox.website.inventory;

import com.limpygnome.projectsandbox.website.ents.Entity;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents an inventory.
 * 
 * @author limpygnome
 */
public class Inventory implements Serializable
{
    public static final long serialVersionUID = 1L;
    
    public transient Entity owner;
    
    public InventoryItem selected;
    public LinkedList<InventoryItem> items;
    
    public Inventory(Entity owner)
    {
        this.owner = owner;
        this.items = new LinkedList<>();
    }
    
    public synchronized boolean add(InventoryItem item)
    {
        // Attempt to find similar inventory item and merge
        InventoryMergeResult result;
        for (InventoryItem iitem : items)
        {
            if (iitem.getClass() == item.getClass())
            {
                result = iitem.merge(item);
                
                switch (result)
                {
                    case ADD:
                        // Do nothing...we should check with all the items
                        break;
                    case DONT_ADD:
                        // Conflicts with another item, dont add it...
                        return false;
                    case MERGED:
                        // Merged with another item, success!
                        return true;
                }
            }
            items.add(item);
        }
        
        // Looks like the item can be added
        items.add(item);
        return true;
    }
}
