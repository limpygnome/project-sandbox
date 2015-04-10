function Effect(texture, width, height, x, y, timeout, fade)
{
    Primitive.call(this, width, height);
	
	this.x = x;
	this.y = y;
	this.setTexture(texture);
	this.created = projectSandbox.currentTime;
	this.timeout = timeout;
	this.fade = fade;
	this.expired = false;
}

Effect.inherits(Entity);

Effect.prototype.logic = function()
{
	// Check if already set to expired
	if (this.expired)
	{
		return;
	}
	
	// Check if the effect has expired
	var lifespan = projectSandbox.currentTime - this.created;
	
	if (lifespan > this.timeout)
	{
		this.expired = true;
	}
	
	// Update opacity for fading effects
	if (this.fade && !this.expired)
	{
		// Calculate new opacity
		this.setAlpha(lifespan / this.timeout);
	}
}

Effect.prototype.isExpired = function()
{
	return this.expired;
}
