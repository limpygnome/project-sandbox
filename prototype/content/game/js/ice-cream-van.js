function IceCreamVan()
{
    Entity.call(this, 32, 64);
    
    // Set custom params for this ent
    this.setTexture("vehicles/ice-cream-van");
	
	// Setup trail
	this.trail = new Trail(
		this,
		"error",
		32,
		32,
		100,
		1000,
		true,
		-2,
		2,
		-2,
		2
	);
}

IceCreamVan.inherits(Entity);

IceCreamVan.prototype.logic = function()
{
	// Update trail
	this.trail.logic();
}
