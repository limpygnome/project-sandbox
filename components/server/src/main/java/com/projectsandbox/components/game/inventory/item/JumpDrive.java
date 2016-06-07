package com.projectsandbox.components.game.inventory.item;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.inventory.InventoryInvokeState;
import com.projectsandbox.components.server.inventory.annotation.InventoryItemTypeId;
import com.projectsandbox.components.server.inventory.item.AbstractInventoryItem;

/**
 * Allows the player to instantly move around the map.
 */
@InventoryItemTypeId(typeId = 200)
public class JumpDrive extends AbstractInventoryItem
{
    public static final long serialVersionUID = 1L;

    // Settings
    private long maxDistance;
    private long distanceStep;
    private long rechargeStep;      // The amount the drive rehcarges each logic cycle

    // State
    private long lastUsedTime;

    public JumpDrive(long maxDistance, long distanceStep, long rechargeStep)
    {
        this.maxDistance = maxDistance;
        this.distanceStep = distanceStep;
        this.rechargeStep = rechargeStep;

        lastUsedTime = 0L;
    }

    @Override
    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        if (invokeState == InventoryInvokeState.INVOKE_ONCE)
        {
        }
    }

    @Override
    public String eventFetchItemText(Controller controller)
    {
        return "todo";
    }

}
