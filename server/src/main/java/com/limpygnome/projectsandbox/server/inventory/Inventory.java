package com.limpygnome.projectsandbox.server.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.inventory.enums.InventoryMergeResult;
import com.limpygnome.projectsandbox.server.inventory.enums.InventorySlotState;
import com.limpygnome.projectsandbox.server.inventory.items.AbstractInventoryItem;
import com.limpygnome.projectsandbox.server.packets.types.inventory.InventoryUpdatesOutboundPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.players.enums.PlayerKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final static Logger LOG = LogManager.getLogger(Inventory.class);

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

    public AbstractInventoryItem selected;
    public LinkedHashMap<Short, AbstractInventoryItem> items;
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
        // Set new owner
        this.owner = owner;
        this.flagReset = true;

        if (owner != null)
        {
            LOG.debug("Owner changed - sid: {}", owner.session.sessionId);
        }
        else
        {
            LOG.debug("Owner changed to no one");
        }
    }

    /**
     *
     * @param slotId Can be null for no item selected
     */
    public void setSelected(Short slotId)
    {
        if (slotId == null)
        {
            this.selected = null;
            this.flagSelectedDirty = true;
            LOG.debug("Selected item set to empty");
        }
        else
        {
            AbstractInventoryItem item = items.get(slotId);

            if (item != null)
            {
                // Reset keys of previous item
                if (selected != null)
                {
                    selected.slot.keyDown = false;
                    selected.slot.keyAlreadyDown = false;
                }

                // Set new item + flag to dirty
                this.selected = item;
                this.flagSelectedDirty = true;

                LOG.debug("Selected item - slot id: {}", slotId);
            }
            else
            {
                LOG.warn("Attempted to set selected to invalid item - slot id: {}", slotId);
            }
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

        // Check state of keys
        checkKeys(controller);

        // Update logic of items
        Iterator<Map.Entry<Short, AbstractInventoryItem>> iterator = items.entrySet().iterator();
        Map.Entry<Short, AbstractInventoryItem> entry;
        AbstractInventoryItem item;
        boolean pendingRemoval;

        while (iterator.hasNext())
        {
            entry = iterator.next();
            item = entry.getValue();

            // Execute logic if not being removed - avoids situation
            // where it updates its own state to avoid death i.e. god-mode
            // - this caused a similar issue to be discovered with ents,
            //   since the logic process is similar
            pendingRemoval = item.slot.slotState == InventorySlotState.PENDING_REMOVE ||
                    item.slot.slotState == InventorySlotState.REMOVED;

            if (!pendingRemoval)
            {
                item.logic(controller);
            }

            // Handle slotState changes
            if (!pendingRemoval && flagReset)
            {
                packet.eventItemCreated(controller, item);
                packet.eventItemChanged(controller, item);
            }
            else
            {
                // Handle state of slot
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

            // Reset state
            item.slot.slotState = InventorySlotState.NONE;
        }

        // Check if to raise selected item change
        if (flagReset || flagSelectedDirty)
        {
            packet.eventItemSelected(controller, selected);
            flagSelectedDirty = false;
        }

        // Reset reset-flag
        if (flagReset)
        {
            flagReset = false;
        }

        // Check we have an owner and data got written to the packet
        if (owner != null && !packet.isEmpty())
        {
            // Send to player / owner of inventory
            packet.send(owner);
        }
    }

    public boolean add(Class[] itemClasses)
    {
        for (Class itemClass : itemClasses)
        {
            add(itemClass);
        }

        return true;
    }

    public boolean add(Class itemClass)
    {
        try
        {
            AbstractInventoryItem itemInstance = (AbstractInventoryItem) itemClass.getConstructor().newInstance();
            return add(itemInstance);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Could not add item from class", ex);
        }
    }

    public boolean add(AbstractInventoryItem item)
    {
        // Attempt to find similar inventory item and merge
        AbstractInventoryItem invItem;
        InventoryMergeResult result;

        for (Map.Entry<Short, AbstractInventoryItem> kv : items.entrySet())
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
                        LOG.debug("Add item - conflict - type id: {}, conflicted slot id: {}", item.typeId, invItem.slot.id);
                        return false;
                    case MERGED:
                        // Merged with another item, success!
                        LOG.debug("Add item - merged - type id: {}, merged slot id: {}", item.typeId, invItem.slot.id);
                        return true;
                }
            }
        }

        // Find next identifier for slot
        Short slotId = nextAvailableIdentifier();

        if (slotId == null)
        {
            LOG.warn("Failed to add item, no available slot identifier");
            return false;
        }

        // Create slot data for item and add
        item.slot = new InventorySlotData(slotId, this);
        items.put(slotId, item);

        LOG.debug("Added item - slot id: {}, type id: {}", slotId, item.typeId);

        // Set as selected if no prior item
        if (selected == null)
        {
            setSelected(slotId);
        }

        return true;
    }

    public Short nextAvailableIdentifier()
    {
        final short maxValue = 255;

        short attempts = 0;
        short slotId = cachedNextAvailableItemId;

        Short assignedSlotId = null;

        // Find next available slot ID
        while(assignedSlotId == null && attempts <= maxValue)
        {
            // Check if free
            if (!items.containsKey(slotId))
            {
                assignedSlotId = slotId;
            }
            else if (slotId == maxValue)
            {
                slotId = 0;
            }
            else
            {
                slotId++;
            }

            // Increment attempts to avoid infinite checking
            attempts++;
        }

        // Update cache of next available ID
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

    public AbstractInventoryItem remove(Short slotId)
    {
        AbstractInventoryItem item = items.get(slotId);

        if (item != null)
        {
            item.slot.slotState = InventorySlotState.PENDING_REMOVE;
            LOG.debug("Slot set to be removed - slot id: {}", slotId);

            // Check if item to be removed is selected
            if (selected == item)
            {
                // Set next selected item as previous or next available item
                Short nextSlotId = null;
                boolean foundRemovedItem = false;
                for (Map.Entry<Short, AbstractInventoryItem> kv : items.entrySet())
                {
                    // Check if the current item is being removed
                    if (!foundRemovedItem && kv.getValue() == item)
                    {
                        foundRemovedItem = true;
                    }

                    // Check if we have a candidate yet
                    if (nextSlotId != null && foundRemovedItem)
                    {
                        break;
                    }
                    else
                    {
                        nextSlotId = kv.getValue().slot.id;
                    }
                }

                // Update selected item
                setSelected(nextSlotId);
            }
        } else
        {
            LOG.warn("Attempted to remove missing slot - slot id: {}", slotId);
        }

        return item;
    }

    private void checkKeys(Controller controller)
    {
        // Must be an item selected and owner
        if (owner == null || selected == null)
        {
            return;
        }

        // Check if to update fire key-down state
        final boolean FIRE = owner.isKeyDown(PlayerKeys.Spacebar);

        if (FIRE != selected.slot.keyDown)
        {
            selected.slot.keyDown = FIRE;
            LOG.debug("Selected item invoke change - key down: {}", FIRE);
        }
    }
}
