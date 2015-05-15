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
		-16,
		-16
	);
}

IceCreamVan.inherits(Entity);

IceCreamVan.prototype.logic = function()
{
	// Update trail
	this.trail.logic();
}

IceCreamVan.prototype.eventDeath = function()
{
	console.error("death called");
	game.effects.createExplosion(this.x, this.y, 16, 4000, -15, 15);
}
