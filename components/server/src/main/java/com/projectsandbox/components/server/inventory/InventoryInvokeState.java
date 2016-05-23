package com.projectsandbox.components.server.inventory;

/**
 * Created by limpygnome on 29/04/15.
 */
public enum InventoryInvokeState
{
    /**
     * Indicates a toggle slotState. This should be set to OFF once handled.
     */
    INVOKE_ONCE,
    /**
     * Indicates invoke slotState is ON - e.g. fire weapon.
     */
    ON,
    /**
     * Indicates invoke slotState is OFF - e.g. stop firing weapon.
     */
    OFF
    ;
}
