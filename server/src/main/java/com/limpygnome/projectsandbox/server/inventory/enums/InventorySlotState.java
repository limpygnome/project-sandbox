package com.limpygnome.projectsandbox.server.inventory.enums;

/**
 * Created by limpygnome on 28/04/15.
 */
public enum InventorySlotState
{
    /**
     * No changes have occurred.
     */
    NONE,
    /**
     * Item has been created.
     */
    CREATED,
    /**
     * Item has been changed e.g. ammo.
     */
    CHANGED,
    /**
     * Item is pending removal.
     */
    PENDING_REMOVE,
    /**
     * Item has been removed.
     */
    REMOVED
}
