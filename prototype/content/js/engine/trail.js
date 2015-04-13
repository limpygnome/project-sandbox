function Trail(primitive, textureName, width, height, delay, lifespan, fade, offsetXMin, offsetXMax, offsetYMin, offsetYMax)
{
	this.primitive = primitive;
	this.prevx = primitive.x;
	this.prevy = primitive.y;
	
	this.textureName = textureName;
	this.width = width;
	this.height = height;
	this.delay = delay;
	this.lifespan = lifespan;
	
	this.offsetXMin = offsetXMin;
	this.offsetXMax = offsetXMax;
	this.offsetYMin = offsetYMin;
	this.offsetYMax = offsetYMax;
	
	this.lastEffect = -1;
}

Trail.prototype.logic = function()
{
	this.moved = this.primitive.x != this.prevx || this.primitive.y != this.prevy;
	this.prevx = this.primitive.x;
	this.prevy = this.primitive.y;
	
	// Only create trail if we move
	if (this.moved)
	{
		var currTime = projectSandbox.currentTime;
		
		if (currTime - this.lastEffect > this.delay)
		{
			// Create effect
			var randX = projectSandbox.utils.rand(this.offsetXMin, this.offsetXMax);
			var randY = projectSandbox.utils.rand(this.offsetYMin, this.offsetYMax);
			
			var effect = new Effect(this.textureName, this.width, this.height, this.primitive.x + randX, this.primitive.y + randY, 1.0, this.lifespan, true);
			effect.rotation = this.primitive.rotation;
			
			projectSandbox.effects.push(effect);	
			
			// Update last time created
			this.lastEffect = currTime;
		}
	}
}
