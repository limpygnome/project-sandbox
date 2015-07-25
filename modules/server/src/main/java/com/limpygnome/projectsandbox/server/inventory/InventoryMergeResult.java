package com.limpygnome.projectsandbox.server.inventory;

/**
 * Used to indicate the result from similar inventory imp being merged.
 * 
 * @author limpygnome
 */
public enum InventoryMergeResult
{
    /**
     * Indicates an item should be added, despite being of a similar type,
     * as its own item.
     */
    ADD,
    /**
     * Indicates the item cannot be picked up.
     */
    DONT_ADD,
    /**
     * Indicates the item has been merged.
     */
    MERGED
}
