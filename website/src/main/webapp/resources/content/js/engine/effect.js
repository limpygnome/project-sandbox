function Effect(texture, width, height, x, y, z, timeout, fade)
{
	Primitive.call(this,
        {
            model: "2d-rect",
            width: width,
            height: height
        }
    );

	this.x = x;
	this.y = y;
	this.z = z;
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
		this.setAlpha(1.0 - (lifespan / this.timeout));
	}

	// Update position if vx/vy defined
	if (this.vx)
	{
		this.x += this.vx;
	}
	if (this.vy)
	{
		this.y += this.vy;
	}
}

Effect.prototype.isExpired = function()
{
	return this.expired;
}
