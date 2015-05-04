function Smg(slotId)
{
    InventoryItem.call(this, slotId);
}

Smg.inherits(InventoryItem);

Smg.prototype.getIcon = function()
{
    return "ui-weapons/smg"
}
