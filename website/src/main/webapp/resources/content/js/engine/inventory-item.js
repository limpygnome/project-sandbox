function InventoryItem(slotId)
{
    this.slotId = slotId;
}

InventoryItem.prototype.packetCreate = function(data, dataView, offset)
{
    // No custom data is read by default
    return offset;
}

InventoryItem.prototype.packetRemove = function(data, dataView, offset)
{
    // No custom data is read by default
    return offset;
}

InventoryItem.prototype.packetChanged = function(data, dataView, offset)
{
    // No custom data is read by default
    return offset;
}
