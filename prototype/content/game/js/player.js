function Player()
{
    Entity.call(this, 16, 20);
    
    // Set custom params for this ent
    this.setTexture("players/default");
	
    this.running = false;
    this.prevx = this.x;
    this.prevy = this.y;
}

Player.inherits(Entity);

Player.prototype.logic = function()
{
    var moved = this.x != this.prevx || this.y != this.prevy;
    
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
    this.prevx = this.x;
    this.prevy = this.y;
	
	projectSandbox.effects.push(
		new Effect("players/default", 32, 32, this.x, this.y, 1000, false)
	);
}
