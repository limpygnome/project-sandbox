projectSandbox.inventory =
{
	inventory: new Array(),
	selected: -1,

	render: function()
	{
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
		var slotId = dataView.getInt8(offset);
		offset += 1;

		var typeId = dataView.getInt16(offset);
		offset += 2;

		return offset;
	},

	packetInventoryItemRemoved: function(data, dataView, offset)
	{
	},

	packetInventoryItemChanged: function(data, dataView, offset)
	{
	},
}
