game.effects =
{
	// Constants
	BULLET_WIDTH: 8,
	BULLET_HEIGHT: 8,
	BULLET_Z: 2,
	BULLET_LIFESPAN: 8000,
	BULLET_FADE: true,

	packet: function(data, dataView, subType)
	{
		var offset = 2;

		// Read X,Y co-ordinates
		var effectX = dataView.getFloat32(offset);
		offset += 4;

		var effectY = dataView.getFloat32(offset);
		offset += 4;

		// Create effect
		switch (subType)
		{
			case "B":
				// Create bullet effect
				var effect = new Effect("error", this.BULLET_WIDTH, this.BULLET_HEIGHT, effectX, effectY, this.BULLET_Z, this.BULLET_LIFESPAN, this.BULLET_FADE);
				effect.rotation = this.randRotation();
				projectSandbox.effects.push(effect);
				console.debug("Created bullet at " + effect.x + " , " + effect.y);
				break;
			default:
				console.error("game/effects - unknown effect sub-type - " + subType);
				break;
		}
	},

	randRotation: function()
	{
		return projectSandbox.utils.rand(0.0, 6.28318531);
	}
}
