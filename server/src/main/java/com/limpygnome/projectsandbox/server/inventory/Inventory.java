package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryInvokeState;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryMergeResult;
import com.limpygnome.projectsandbox.server.inventory.enums.InventorySlotState;
import com.limpygnome.projectsandbox.server.packets.types.inventory.InventoryUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an inventory.
 *
 * @author limpygnome
 */
public class Inventory implements Serializable
{
    public static final long serialVersionUID = 1L;

    /**
     * Indicates if selected item is dirty / has changed.
     */
    private boolean flagSelectedDirty;

    /**
     * Indicates if to send a reset packet to the current entity i.e. new player.
     */
    private boolean flagReset;

    // TODO: should this be transient? How to connect them up?
    public transient Entity parent;

    // TODO: should this be transient? How to connect them up?
    private PlayerInfo owner;

    public InventoryItem selected;
    public LinkedHashMap<Short, InventoryItem> items;
    private short cachedNextAvailableItemId;

    public Inventory(Entity parent)
    {
        this.parent = parent;
        this.flagSelectedDirty = false;
        this.flagReset = true;
        this.selected = null;
        this.items = new LinkedHashMap<>();
        this.cachedNextAvailableItemId = 0;
    }

    public void setOwner(PlayerInfo owner)
    {
        this.owner = owner;
        this.flagReset = true;
    }

    public void setSelected(Short slotId)
    {
        InventoryItem item = items.get(slotId);
        if (item != null)
        {
            this.selected = item;
            this.flagSelectedDirty = true;
        }
    }

    public void logic(Controller controller)
    {
        // Build packet as we execute logic, saves on computation
        InventoryUpdatesOutboundPacket packet = new InventoryUpdatesOutboundPacket();

        // Check if to raise inventory reset
        if (flagReset)
        {
            packet.eventReset();
        }

        // Check if to raise selected item change
        if (flagSelectedDirty)
        {
            packet.eventSelected(controller, selected);
        }

        // Update logic of items
        Iterator<Map.Entry<Short, InventoryItem>> iterator = items.entrySet().iterator();
        Map.Entry<Short, InventoryItem> entry;
        InventoryItem item;

        while (iterator.hasNext())
        {
            entry = iterator.next();
            item = entry.getValue();

            // Execute logic
            item.logic(controller);

            // Handle slotState changes
            switch (item.slot.slotState)
            {
                case CREATED:
                    packet.eventItemCreated(controller, item);
                    break;
                case CHANGED:
                    packet.eventItemChanged(controller, item);
                    break;
                case PENDING_REMOVE:
                    packet.eventItemRemoved(controller, item);
                    break;
                case REMOVED:
                    // Remove from collection - no longer needed
                    iterator.remove();
                    break;
                case NONE:
                    // Do nothing...
                    break;
                default:
                    throw new RuntimeException("Unhandled item slot");
            }
        }

        // Send packet to current player of inventory
        if (owner != null)
        {
            try
            {
                // Write data
                packet.build();

                // Send to player
                packet.send(owner);
            }
            catch (IOException e)
            {
                // TODO: log exception
            }
        }
    }

    public boolean add(InventoryItem item)
    {
        // Attempt to find similar inventory item and merge
        InventoryItem invItem;
        InventoryMergeResult result;

        for (Map.Entry<Short, InventoryItem> kv : items.entrySet())
        {
            invItem = kv.getValue();

            if (item.getClass() == invItem.getClass())
            {
                result = invItem.merge(item);

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
        }

        // Find next identifier for slot
        Short slotId = nextAvailableIdentifier();

        if (slotId == null)
        {
            // TODO: logging
            return false;
        }

        // Create slot data for item and add
        item.slot = new InventorySlotData(slotId, this);
        items.put(slotId, item);

        // Set as selected if no prior item
        if (selected == null)
        {
            selected = item;
            flagSelectedDirty = true;
        }

        return true;
    }

    public Short nextAvailableIdentifier()
    {
        final short maxValue = 255;

        short attempts = 0;
        short slotId = cachedNextAvailableItemId;

        // Find next available slot ID
        while(!items.containsKey(slotId) && attempts <= maxValue)
        {
            if (slotId == maxValue)
            {
                slotId = 0;
            }
            else
            {
                slotId++;
            }
            attempts++;
        }

        // Check we didn't exceed max attempts
        if (attempts > maxValue)
        {
            return null;
        }

        // Update cache of next ID
        if (slotId == maxValue)
        {
            cachedNextAvailableItemId = 0;
        }
        else
        {
            cachedNextAvailableItemId++;
        }

        return slotId;
    }

    public InventoryItem remove(Short slotId)
    {
        InventoryItem item = items.get(slotId);

        if (item != null)
        {
            item.slot.slotState = InventorySlotState.PENDING_REMOVE;

            if (selected == item)
            {
                selected = null;
                flagSelectedDirty = true;
            }
        }

        return item;
    }

    public void selectedInvoke(Controller controller, boolean keyDown)
    {
        if (selected != null)
        {
            selected.slot.keyDown = keyDown;
        }
    }

    public void selectedSet(Controller controller, short slotId, boolean keyDown)
    {
        InventoryItem item = items.get(slotId);

        if (item != null)
        {
            // Set existing item's state to false for keydown
            if (selected != null)
            {
                selected.slot.keyDown = false;
                selected.slot.keyAlreadyDown = false;
            }

            // Set key state
            item.slot.keyDown = keyDown;

            // Item is now selected
            selected = item;
        }
        else
        {
            // TODO: logging
        }
    }
}
