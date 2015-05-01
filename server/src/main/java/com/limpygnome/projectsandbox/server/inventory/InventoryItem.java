package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.annotations.InventyoryItemTypeId;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeState;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeType;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryMergeResult;

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

    public InventorySlotData slot;
    public InventoryInvokeType invokeType;
    
    public InventoryItem()
    {
        this.slot = null;
        this.invokeType = InventoryInvokeType.FIRE_ONCE;
    }

    public void logic(Controller controller)
    {
        switch (invokeType)
        {
            case TOGGLE:
                if (slot.keyDown)
                {
                    eventInvoke(controller, InventoryInvokeState.ON);
                }
                else
                {
                    eventInvoke(controller, InventoryInvokeState.OFF);
                }
                break;
            case FIRE_ONCE:
                if (!slot.keyAlreadyDown && slot.keyDown)
                {
                    eventInvoke(controller, InventoryInvokeState.INVOKE_ONCE);
                }
                else if (slot.keyAlreadyDown && !slot.keyDown)
                {
                    slot.keyAlreadyDown = false;
                    eventInvoke(controller, InventoryInvokeState.OFF);
                }
                break;
            default:
                throw new IllegalArgumentException("Unhandled invoke type");
        }
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

    public void eventInvoke(Controller controller, InventoryInvokeState invokeState)
    {
        // Nothing by default...
    }

    public void eventInventoryWritePacketCreated(Controller controller, LinkedList<Object> packetData) { }
    
    public void eventInventoryWritePacketRemoved(Controller controller, LinkedList<Object> packetData) { }

    public void eventInventoryWritePacketChanged(Controller controller, LinkedList<Object> packetData) { }
    
    public final short getTypeId()
    {
        return typeId;
    }
}
