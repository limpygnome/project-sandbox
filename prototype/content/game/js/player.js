function Player()
{
    Entity.call(this, 16, 20);
    
    // Set custom params for this ent
    this.setTexture("players/default");
	
    this.running = false;
    this.prevx = this.x;
    this.prevy = this.y;
	
	this.lastEffect = -1;
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
	
	// Create movement particles
	var currTime = projectSandbox.currentTime;
	if (moved && currTime - this.lastEffect > 400)
	{
		this.lastEffect = currTime;
		
		var randX = projectSandbox.utils.rand(-2, 2);
		var randY = projectSandbox.utils.rand(-2, 2);
		
		projectSandbox.effects.push(
			new Effect("error", 16, 16, this.x + randX, this.y + randY, 1.0, 2000, true)
		);
	}
    
    // Update running state
    this.running = moved;
    this.prevx = this.x;
    this.prevy = this.y;
}
