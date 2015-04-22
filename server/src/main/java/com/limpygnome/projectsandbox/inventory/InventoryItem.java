package com.limpygnome.projectsandbox.inventory;

/**
 *
 * @author limpygnome
 */
public abstract class InventoryItem
{
    public Inventory inventory;
    
    public InventoryItem(Inventory inventory)
    {
        this.inventory = inventory;
    }
}
