function IceCreamVan()
{
    Entity.call(this,
    	{
    		model: "3d-cube",
    		width: 32.0,
    		height: 64.0,
    		depth: 10.0
    	}
    );

    // Set custom params for this ent
    //this.setTexture("vehicles/ice-cream-van");
    this.setTexture("world/building");

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
	game.effects.createExplosion(this.x, this.y, 128, 4000, -2, 2);
}
