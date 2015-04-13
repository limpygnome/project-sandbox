function Player()
{
    Entity.call(this, 16, 20);
    
    // Set custom params for this ent
    this.setTexture("players/default");
	
    this.running = false;
    this.prevx = this.x;
    this.prevy = this.y;
	
	this.trail = new Trail(
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
    var moved = this.x != this.prevx || this.y != this.prevy;
    
	// Update texture
    if (moved && !this.running)
    {
        this.setTexture("players/default_running");
    }
    else if (!moved && this.running)
    {
        this.setTexture("players/default");
    }
	
	// Update trail
	if (moved)
	{
		this.trail.logic(this.x, this.y, this.rotation);
	}
    
    // Update running state
    this.running = moved;
    this.prevx = this.x;
    this.prevy = this.y;
}
