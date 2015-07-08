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
}

AbstractPickup.inherits(Entity);

AbstractPickup.prototype.logic = function()
{
	// Bob up and down
    // TODO: INHERIT OFF A PICKUP CLASS TO DO THIS...FOR ALL PICKUPS!
    console.log("logic");
}
