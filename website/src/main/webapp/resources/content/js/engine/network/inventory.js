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
				case "R":
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
        var inventory = projectSandbox.inventory;

        inventory.reset();

        return offset;
    },

	packetInventoryItemSelected: function(data, dataView, offset)
	{
	    var inventory = projectSandbox.inventory;

		inventory.selectedSlotId = dataView.getInt8(offset);

		console.debug("engine/network/inventory - item selected - " + projectSandbox.inventory.selectedSlotId);

		// Call UI hook
		projectSandbox.game.ui.hookInventory_selectedChanged();

		return offset + 1;
	},

	packetInventoryItemNonSelected: function(data, dataView, offset)
	{
	    var inventory = projectSandbox.inventory;

		inventory.selected = -1;

		console.debug("engine/network/inventory - no item selected");

		return offset;
	},

	packetInventoryItemCreated: function(data, dataView, offset)
	{
	    var inventory = projectSandbox.inventory;

		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		var typeId = dataView.getInt16(offset);
		offset += 2;

		// Check it doesn't already exist
		if (inventory.items.has(slotId))
		{
			console.error("engine/network/inventory - unable to add duplicate slot to inventory - " + slotId);
			return;
		}

		// Create type
		var item;

		switch (typeId)
		{
		    // Fist
		    case 1:
		        item = new Fist(slotId);
		        console.debug("engine/network/inventory - created fist - " + slotId);
		        break;
			// Weapons -> SMG
			case 100:
				item = new Smg(slotId);
				console.debug("engine/network/inventory - created SMG - " + slotId);
				break;
			default:
				console.error("engine/network/inventory - cannot create item - no type exists - " + typeId);
				return offset;
		}

		// Allow item to read custom data
		offset = item.packetCreate(data, dataView, offset);

		// Add to inventory
		inventory.items.set(slotId, item);
		inventory.renderOrder.push(slotId);

		return offset;
	},

	packetInventoryItemRemoved: function(data, dataView, offset)
	{
	    var inventory = projectSandbox.inventory;

		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		// Fetch item
		var item = inventory.items.get(slotId);

        if (item != null)
        {
            // Allow item to read custom data
            offset = item.packetRemove(data, dataView, offset);

            // Remove from collections
            inventory.items.delete(slotId);
            inventory.renderOrder.splice(item, 1);

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
	    var inventory = projectSandbox.inventory;

		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		// Fetch item
		var item = inventory.items.get(slotId);

        if (item != null)
        {
		    // Allow item to read custom data
            offset = item.packetChanged(data, dataView, offset);

            console.debug("engine/network/inventory - item changed - " + slotId);
		}
		else
		{
		    console.error("engine/network/inventory - change occurred for missing item: " + slotId);
		}

		return offset;
	}
}
