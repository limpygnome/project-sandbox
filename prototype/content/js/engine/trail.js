function Trail(textureName, width, height, delay, lifespan, fade, offsetXMin, offsetXMax, offsetYMin, offsetYMax)
{
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

Trail.prototype.logic = function(x, y, rotation)
{
	// Create movement particles
	var currTime = projectSandbox.currentTime;
	
	if (currTime - this.lastEffect > this.delay)
	{
		// Create effect
		var randX = projectSandbox.utils.rand(this.offsetXMin, this.offsetXMax);
		var randY = projectSandbox.utils.rand(this.offsetYMin, this.offsetYMax);
		
		var effect = new Effect(this.textureName, this.width, this.height, x + randX, y + randY, 1.0, this.lifespan, true);
		effect.rotation = rotation;
		
		projectSandbox.effects.push(effect);	
		
		// Update last time created
		this.lastEffect = currTime;
	}
}
