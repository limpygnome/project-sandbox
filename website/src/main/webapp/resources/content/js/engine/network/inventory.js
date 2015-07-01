projectSandbox.network.inventory =
{
    packet: function(subType, data)
	{
		switch (subType)
		{
		    // Updates
			case 'U':
				this.packetInventoryUpdates(data);
				return;

			default:
				console.error("engine/network/inventory - unknown sub-type - " + subType);
				break;
		}
	},

	packetInventoryUpdates: function(data)
	{
		var dataView = new DataView(data.buffer);

		var offset = 2; // maintype/subtype

		var updateType;

		while (offset < data.length)
		{
			// Read the type of update
			updateType = String.fromCharCode(dataView.getInt8(offset));
			offset += 1;

			// Handle the rest of the data based on the type
			switch (updateType)
			{
				case "R":
					offset = this.packetInventoryReset(data, dataView, offset);
					break;
				case "S":
					offset = this.packetInventoryItemSelected(data, dataView, offset);
					break;
				case "N":
					offset = this.packetInventoryItemNonSelected(data, dataView, offset);
					break;
				case "C":
					offset = this.packetInventoryItemCreated(data, dataView, offset);
					break;
				case "D":
					offset = this.packetInventoryItemRemoved(data, dataView, offset);
					break;
				case "M":
					offset = this.packetInventoryItemChanged(data, dataView, offset);
					break;
                default:
                    console.error("Inventory - unhandled update type - " + updateType);
                    break;
			}
		}
	},

    packetInventoryReset: function(data, dataView, offset)
    {
        // Reset internal inventory
        var inventory = projectSandbox.inventory;
        inventory.reset();

        // Inform UI to reset
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventoryReset();

        return offset;
    },

	packetInventoryItemSelected: function(data, dataView, offset)
	{
	    // Read data
	    var slotId = dataView.getInt8(offset);

	    // Update internal selected slot
	    var inventory = projectSandbox.inventory;
		inventory.selectedSlotId = slotId;

        // Fetch inventory item
        var inventoryItem = inventory.items.get(slotId);

        // Inform UI
        var gameUI = projectSandbox.game.ui;
        gameUI.hook_inventorySlotSelected(inventoryItem);

        console.debug("engine/network/inventory - item selected - " + slotId);

		return offset + 1;
	},

	packetInventoryItemNonSelected: function(data, dataView, offset)
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

	packetInventoryItemCreated: function(data, dataView, offset)
	{
	    // Read data
	    var slotId = dataView.getInt8(offset);
        offset += 1;

        var typeId = dataView.getInt16(offset);
        offset += 2;

        var textLen = dataView.getInt8(offset);
		var text = String.fromCharCode.apply(String, data.subarray(offset + 1, offset + 1 + textLen));
		offset += 1 + textLen;

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

		return offset;
	},

	packetInventoryItemRemoved: function(data, dataView, offset)
	{
	    // Read data
	    var slotId = dataView.getInt8(offset);
        offset += 1;

		// Fetch item
		var inventory = projectSandbox.inventory;
		var inventoryItem = inventory.items.get(slotId);

        if (inventoryItem != null)
        {
            // Remove from collections
            inventory.items.delete(slotId);
            inventory.renderOrder.splice(inventoryItem, 1);

            // Inform UI
            var gameUI = projectSandbox.game.ui;
            gameUI.hook_inventorySlotRemove(inventoryItem);

            console.debug("engine/network/inventory - removed item - " + slotId);
		}
		else
		{
		    console.error("engine/network/inventory - attempted to remove missing item: " + slotId);
		}

		return offset;
	},

	packetInventoryItemChanged: function(data, dataView, offset)
	{
	    // Read data
	    var slotId = dataView.getInt8(offset);
        offset += 1;

        var textLen = dataView.getInt8(offset);
		var text = String.fromCharCode.apply(String, data.subarray(offset + 1, offset + 1 + textLen));
		offset += 1 + textLen;

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

		return offset;
	}
}
