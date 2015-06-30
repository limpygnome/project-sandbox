game.effects =
{
	// Constants
	BULLET_WIDTH: 8,
	BULLET_HEIGHT: 8,
	BULLET_Z: -0.5,
	BULLET_LIFESPAN: 8000,
	BULLET_FADE: true,

	TRACER_WIDTH: 1,
	TRACER_Z: -0.5,
	TRACER_LIFESPAN: 1000,
	TRACER_FADE: true,

	packet: function(data, dataView)
	{
		var offset = 1;

		var subType;
		while (offset < data.length)
		{
			subType = String.fromCharCode(data[offset]);
			offset += 1;

			// Create effect
			switch (subType)
			{
				case "B":
					// Create bullet effect
					var effectX = dataView.getFloat32(offset);
					var effectY = dataView.getFloat32(offset + 4);

					var effect = new Effect("error", this.BULLET_WIDTH, this.BULLET_HEIGHT, effectX, effectY, this.BULLET_Z, this.BULLET_LIFESPAN, this.BULLET_FADE);
					effect.rotation = projectSandbox.utils.randRotation();
					projectSandbox.effects.push(effect);
					console.debug("game/effects - created bullet at " + effect.x + " , " + effect.y);

					offset += 8;
					break;
				case "T":
					offset += this.packetCreateTracer(data, dataView, offset);
					break;
				case "E":
					offset += this.packetCreateExplosion(data, dataView, offset);
					break;
				default:
					console.error("game/effects - unknown effect sub-type - " + subType);
					break;
			}
		}
	},

	packetCreateTracer: function(data, dataView, offset)
	{
		// Create tracer effect
		var x1 = dataView.getFloat32(offset + 0);
		var y1 = dataView.getFloat32(offset + 4);
		var x2 = dataView.getFloat32(offset + 8);
		var y2 = dataView.getFloat32(offset + 12);

		// - Compute size
		var w = Math.abs(x2 - x1);
		var h = Math.abs(y2 - y1);
		var size = Math.sqrt( (w*w) + (h*h) );

		// - Compute midpoint for x,y
		var midx = x1 + ((x2 - x1) / 2.0);
		var midy = y1 + ((y2 - y1) / 2.0);

		// - Compute rotation
		var rotation = Math.atan2(x2 - x1, y2 - y1);

		// - Finally create the effect
		var effect = new Effect("error", this.TRACER_WIDTH, size, midx, midy, this.TRACER_Z, this.TRACER_LIFESPAN, this.TRACER_FADE);
		effect.rotation = rotation;
		projectSandbox.effects.push(effect);

		return 16;
	},

	packetCreateExplosion: function(data, dataView, offset)
	{
		// Parse data
		var x = dataView.getFloat32(0);
		var y = dataView.getFloat32(4);
		var subType = dataView.getInt8(8);

		// Create explosion based on subtype
		switch (subType)
		{
			// Allah ackbar
			case 100:
				this.createExplosion(
					x, y, 512, 4000, 6.0, 6.0
				);
				break;
			default:
				console.error("game/effects - unknown explosion effect sub-type - " + subType);
				break;
		}

		return 9;
	},

	EXPLOSION_Z: -0.5,
	EXPLOSION_FADE: true,
	EXPLOSION_WIDTH: 16,
	EXPLOSION_HEIGHT: 16,

	createExplosion: function(x, y, particles, lifeSpan, velocityMin, velocityMax)
	{
		var effect;
		for (var i = 0; i < particles; i++)
		{
			effect = new Effect("error", this.EXPLOSION_WIDTH, this.EXPLOSION_HEIGHT, x, y, this.EXPLOSION_Z, lifeSpan, this.EXPLOSION_FADE);
			effect.rotation = projectSandbox.utils.randRotation();
			effect.vx = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);
			effect.vy = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);
			effect.vz = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);
			projectSandbox.effects.push(effect);
		}
	}

}
