function AbstractPickup(width, height, depth)
{
    Entity.call(this,
    	{
    		model: "3d-cube",
    		width: width,
    		height: height,
    		depth: depth
    	}
    );

    this.offsetZ = 0.0;
    this.offsetFactor = 0.5;
    this.offsetZMin = -5.0;
    this.offsetZMax = 12.0;
    this.offsetDirectionPositive = true;
    this.spinFactor = 0.08;
}

AbstractPickup.inherits(Entity);

AbstractPickup.prototype.logic = function()
{
	// Bob up and down
	var offsetAmount = this.offsetDirectionPositive ? this.offsetFactor : -this.offsetFactor;

	// -- Increment internal counter
	var oldOffsetZ = this.offsetZ;
    this.offsetZ += offsetAmount;

    // -- Bound to minimum
    if (this.offsetZ < this.offsetZMin)
    {
    	this.offsetZ = this.offsetZMin;
    	this.offsetDirectionPositive = true;
    }
    // -- Bound to maximum
    else if (this.offsetZ > this.offsetZMax)
    {
    	this.offsetZ = this.offsetZMax;
    	this.offsetDirectionPositive = false;
    }

    // -- Apply final offset amount to Z by subtracting the difference between the old and new internal offset amounts
    this.z += this.offsetZ - oldOffsetZ;

    // Spin around!
    this.rotation = projectSandbox.utils.clampCircular(
        -projectSandbox.utils.PI,
        +projectSandbox.utils.PI,
        this.rotation + this.spinFactor
    );

}
