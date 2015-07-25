package com.limpygnome.projectsandbox.server.inventory.enums;

/**
 * Created by limpygnome on 30/04/15.
 */
public enum InventoryInvokeType
{
    /**
     * Indicates a single invocation of the item toggles the ON/OFF state.
     */
    TOGGLE,
    /**
     * Indicates a single invocation causes the state to immediately go from OFF to ON to off.
     */
    FIRE_ONCE
}
