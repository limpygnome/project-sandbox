function Gatling(slotId)
{
    InventoryItem.call(this, slotId);
}

Gatling.inherits(InventoryItem);

Gatling.prototype.getIcon = function()
{
    return "ui-weapons/gatling"
}
