projectSandbox.network.inventory =
{
    handlePacket: function(packet)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            // Updates
            case 'U':
                this.packetInventoryUpdates(packet);
                return;

            default:
                console.error("engine/network/inventory - unknown sub-type - " + subType);
                break;
        }
    },

    packetInventoryUpdates: function(packet)
    {
        var updateType;

        while (packet.hasMoreData())
        {
            // Read the type of update
            updateType = packet.readChar();

            // Handle the rest of the data based on the type
            switch (updateType)
            {
                case "R":
                    offset = this.packetInventoryReset(packet);
                    break;
                case "S":
                    offset = this.packetInventoryItemSelected(packet);
                    break;
                case "N":
                    offset = this.packetInventoryItemNonSelected(packet);
                    break;
                case "C":
                    offset = this.packetInventoryItemCreated(packet);
                    break;
                case "D":
                    offset = this.packetInventoryItemRemoved(packet);
                    break;
                case "M":
                    offset = this.packetInventoryItemChanged(packet);
                    break;
                default:
                    console.error("Inventory - unhandled update type - " + updateType);
                    break;
            }
        }
    },

    packetInventoryReset: function(packet)
    {
        // Reset internal inventory
        var inventory = projectSandbox.inventory;
        inventory.reset();

        // Inform UI to reset
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventoryReset();
    },

    packetInventoryItemSelected: function(packet)
    {
        // Read data
        var slotId = packet.readByte();

        // Update internal selected slot
        var inventory = projectSandbox.inventory;
        inventory.selectedSlotId = slotId;

        // Fetch inventory item
        var inventoryItem = inventory.items.get(slotId);

        // Inform UI
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventorySlotSelected(inventoryItem);

        console.debug("engine/network/inventory - item selected - " + slotId);
    },

    packetInventoryItemNonSelected: function(packet)
    {
        // Update internal inventory
        var inventory = projectSandbox.inventory;
        inventory.selected = -1;

        // Inform UI
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventorySlotSelected(null);

        console.debug("engine/network/inventory - no item selected");

        return offset;
    },

    packetInventoryItemCreated: function(packet)
    {
        // Read data
        var slotId = packet.readByte();
        var typeId = packet.readShort();
        var text = packet.readAscii();

        // Check it doesn't already exist
        var inventory = projectSandbox.inventory;

        if (inventory.items.has(slotId))
        {
            console.error("engine/network/inventory - unable to add duplicate slot to inventory - " + slotId);
            return;
        }

        // Create inventory item
        var inventoryItem = new InventoryItem(slotId, typeId, text);

        // Add to inventory
        inventory.items.set(slotId, inventoryItem);
        inventory.renderOrder.push(slotId);

        // Inform UI
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventorySlotCreate(inventoryItem);
    },

    packetInventoryItemRemoved: function(packet)
    {
        // Read data
        var slotId = packet.readByte();

        // Fetch item
        var inventory = projectSandbox.inventory;
        var inventoryItem = inventory.items.get(slotId);

        if (inventoryItem != null)
        {
            // Remove from collections
            inventory.items.delete(slotId);

            var renderOrderIndex = inventory.findItemIndex(slotId);
            if (renderOrderIndex != null)
            {
                inventory.renderOrder.splice(renderOrderIndex, 1);
            }
            else
            {
                console.warn("engine/network/inventory - no render order inventory item present - slot id: " + slotId);
            }

            // Inform UI
            var gameUI = projectSandbox.game.ui;
            gameUI.hook_inventorySlotRemove(inventoryItem);

            console.debug("engine/network/inventory - removed item - " + slotId);
        }
        else
        {
            console.error("engine/network/inventory - attempted to remove missing item: " + slotId);
        }
    },

    packetInventoryItemChanged: function(packet)
    {
        // Read data
        var slotId = packet.readByte();
        var text = packet.readAscii();

        // Fetch item
        var inventory = projectSandbox.inventory;
        var inventoryItem = inventory.items.get(slotId);

        if (inventoryItem != null)
        {
            // Update item
            inventoryItem.text = text;

            // Inform UI
            var gameUI = projectSandbox.game.ui;
            gameUI.hook_inventorySlotUpdate(inventoryItem);

            console.debug("engine/network/inventory - item changed - " + slotId);
        }
        else
        {
            console.error("engine/network/inventory - change occurred for missing item: " + slotId);
        }
    }

}
