function Fist(slotId)
{
    InventoryItem.call(this, slotId);
}

Fist.inherits(InventoryItem);

Fist.prototype.getIcon = function()
{
    return "ui-weapons/fist"
}
