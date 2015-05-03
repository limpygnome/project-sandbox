function Player()
{
    Entity.call(this, 16, 20);
    
    // Set custom params for this ent
    this.setTexture("players/default");
	
    this.running = false;
	
	this.trail = new Trail(
		this,
		"error",
		16,
		16,
		400,
		1500,
		true,
		-2,
		2,
		-2,
		2
	);
}

Player.inherits(Entity);

Player.prototype.logic = function()
{
	// Update trail
	this.trail.logic(this);
	
	// Update running state
    var moved = this.trail.moved;
    
    if (moved && !this.running)
    {
        this.setTexture("players/default_running");
    }
    else if (!moved && this.running)
    {
        this.setTexture("players/default");
    }
	
    // Update running state
    this.running = moved;
}
