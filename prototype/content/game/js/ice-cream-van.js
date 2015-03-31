function IceCreamVan()
{
    Entity.call(this, 32, 64);
    
    // Set custom params for this ent
    this.setTexture("vehicles/ice-cream-van");
}

IceCreamVan.inherits(Entity);

IceCreamVan.prototype.logic = function()
{
	// TODO: create smoke particles behind van
}
