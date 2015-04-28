package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.packets.outbound.inventory.InventoryUpdatesPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

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
    public boolean flagSelectedDirty;

    /**
     * Indicates if to send a reset packet to the current entity i.e. new player.
     */
    public boolean flagReset;

    // TODO: should this be transient?
    public transient Entity parent;

    private PlayerInfo owner;

    public InventoryItem selected;
    public LinkedList<InventoryItem> items;

    public Inventory(Entity parent)
    {
        this.parent = parent;
        this.flagSelectedDirty = false;
        this.flagReset = false;
        this.selected = null;
        this.items = new LinkedList<>();
    }

    public void setOwner(PlayerInfo owner)
    {
        this.owner = owner;
        this.flagReset = true;
    }

    public void logic(Controller controller)
    {
        // Build packet as we execute logic, saves on computation
        InventoryUpdatesPacket packet = new InventoryUpdatesPacket();

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
        Iterator<InventoryItem> iterator = items.iterator();
        InventoryItem item;

        while (iterator.hasNext())
        {
            item = iterator.next();

            // Execute logic
            item.logic(controller);

            // Handle state changes
            switch (item.slot.state)
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
                packet.finalize();

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

        // Find next identifier for slot
        int slotId;

        // Create slot data for item
        item.slot = new InventorySlotData(slotId);

        // Looks like the item can be added
        items.add(item);
        return true;
    }
}
