function Bus()
{
    Entity.call(this,
    	{
    		model: "3d-cube",
    		width: 28.0,
    		height: 96.0,
    		depth: 12.0
    	}
    );

    // Set custom params for this ent
    //this.setTexture("vehicles/ice-cream-van");
    this.setTexture("world/building");

	// Setup trail
	this.trail = new Trail(
		this,
		"error",
		34,
		34,
		100,
		3000,
		true,
		-2,
		2,
		-16,
		-16
	);
}

Bus.inherits(Entity);

Bus.prototype.logic = function()
{
	// Update trail
	this.trail.logic();
}

Bus.prototype.eventDeath = function()
{
	game.effects.createExplosion(this.x, this.y, 256, 6000, -1.5, 1.5);
}
