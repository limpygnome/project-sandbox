package com.limpygnome.projectsandbox.inventory;

import com.limpygnome.projectsandbox.ents.Entity;

/**
 *
 * @author limpygnome
 */
public class Inventory
{
    public Entity owner;
    public InventoryItem[] items;
    
    public Inventory(Entity owner)
    {
        this.owner = owner;
        this.items = new InventoryItem[InventoryConstants.DEFAULT_ITEMS];
    }
    
    public synchronized boolean add(InventoryItem item)
    {
        // TODO: consider optimisation
        for (int i = 0; i < items.length; i++)
        {
            if (items[i] == null)
            {
                items[i] = item;
                return true;
            }
        }
        return false;
    }
}
