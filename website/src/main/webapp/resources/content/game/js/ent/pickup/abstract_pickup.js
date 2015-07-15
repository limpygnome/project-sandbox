function AbstractPickup()
{
    Entity.call(this,
    	{
    		model: "3d-cube",
    		width: 24.0,
    		height: 8.0,
    		depth: 24.0
    	}
    );

    this.offsetZ = 0.0;
    this.offsetFactor = 0.1;
    this.offsetZMin = -10.0;
    this.offsetZMax = 10.0;
    this.offsetDirectionPositive = true;
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
}
