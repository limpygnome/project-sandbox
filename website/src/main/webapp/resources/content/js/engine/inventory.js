projectSandbox.inventory =
{
	// Array of slot IDs to retain order in which to render items
	renderOrder: new Array(),

	// K (slotid), V (item)
	items: new Map(),

	// Slot ID selected
	selected: -1,

	render: function()
	{
	},

	packetInventory: function(subType, data)
	{
		switch (subType)
		{
			case 'R': // Reset
				this.packetInventoryReset(data);
				return;
			case 'U': // Updates
				this.packetInventoryUpdates(data);
				return;
			default:
				console.error("Inventory - unhandled packet - " + subType);
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
			updateType = dataView.getInt8(offset);
			offset += 1;

			// Handle the rest of the data based on the type
			switch (updateType)
			{
				case 00:
					offset = this.packetInventoryReset(data, dataView, offset);
					break;
				case 00:
					offset = this.packetInventoryItemSelected(data, dataView, offset);
					break;
				case 00:
					offset = this.packetInventoryItemNonSelected(data, dataView, offset);
					break;
				case 00:
					offset = this.packetInventoryItemCreated(data, dataView, offset);
					break;
				case 00:
					offset = this.packetInventoryItemRemoved(data, dataView, offset);
					break;
				case 00:
					offset = this.packetInventoryItemChanged(data, dataView, offset);
					break;
			}
		}
	},

	packetInventoryReset: function(data, dataView, offset)
	{
		this.inventory = new Array();

		return offset;
	},

	packetInventoryItemSelected: function(data, dataView, offset)
	{
		this.selected = dataView.getInt8(offset);

		return offset + 1;
	},

	packetInventoryItemNonSelected: function(data, dataView, offset)
	{
		this.selected = -1;

		return offset;
	},

	packetInventoryItemCreated: function(data, dataView, offset)
	{
		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		var typeId = dataView.getInt16(offset);
		offset += 2;

		// Check it doesn't already exist
		if (this.items.has(slotId))
		{
			console.error("Unable to add duplicate slot to inventory - " + slotId);
			return;
		}

		// Create type
		var item;

		switch (typeId)
		{
			// Weapons -> SMG
			case 100:
				break;
			default:
				console.error("Inventory - no type exists - " + typeId);
				break;
		}

		// Allow item to read custom data

		// Add to inventory
		this.items.set(slotId, item);

		return offset;
	},

	packetInventoryItemRemoved: function(data, dataView, offset)
	{
		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		// Allow item to read custom data

		// Remove from collections
		this.items.delete(slotId);
		this.renderOrder.splice(item, 1);
	},

	packetInventoryItemChanged: function(data, dataView, offset)
	{
		// Parse mandatory data
		var slotId = dataView.getInt8(offset);
		offset += 1;

		// Allow item to read custom data
	},
}
