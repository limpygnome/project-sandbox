projectSandbox.network.entities =
{
	packet: function(data, dataView, subType)
	{
		switch (subType)
		{
			case "U":
				this.packetUpdates(data, dataView);
				break;
			default:
				console.error("engine/network/entities - unknown sub-type - " + subType);
				break;
		}
	},

	packetUpdates: function(data, dataView)
	{
		var offset = 2; // maintype / subtype = 2 bytes

		var updateType;
		var id;
		var bytesRead;

		while (offset < data.length)
		{
			// Retrieve update type and entity id
			updateType = String.fromCharCode(dataView.getInt8(offset));
			id = dataView.getInt16(offset + 1);

			// Increment offset
			offset += 3; // 1 byte for update type, 2 bytes for id

			// Handle update based on type
			switch(updateType)
			{
				case "C":
					bytesRead = this.packetUpdatesEntCreated(data, dataView, id, offset);
					break;
				case "U":
					bytesRead = this.packetUpdatesEntUpdated(data, dataView, id, offset);
					break;
				case "D":
					bytesRead = this.packetUpdatesEntDeleted(data, dataView, id, offset);
					break;
				default:
					console.log("engine/network/entities - unknown entity update type '" + updateType + "'");
					break;
			}

			// Increment offset
			offset += bytesRead; // 1 byte for update type, 2 bytes for ent id
		}
	},

	packetUpdatesEntCreated: function(data, dataView, id, offset)
	{
		var originalOffset = offset;

		// Parse data
		var entityType = dataView.getInt16(offset);
		offset += 2;
		var maxHealth = dataView.getFloat32(offset);
		offset += 4;

		// Create entity based on type
		var ent = null;
		switch(entityType)
		{
			default:
				console.warn("engine/network/entities - unhandled ent type " + entityType);
				break;
			case 0:
				ent = new Entity();
				break;
			case 1:
				ent = new Player();
				break;
			case 500:
				ent = new Sentry();
				break;
			case 600:
				ent = new Rocket();
				break;
			case 20:
				ent = new IceCreamVan();
				break;
			case 21:
				ent = new RocketCar();
				break;
			case 22:
				ent = new Bus();
				break;
			case 1201:
				ent = new HealthPickup();
				break;
		}

		if (ent != null)
		{
			// Set max health
			ent.maxHealth = maxHealth;

			// TODO: read custom byte data here

			// Add to world
			projectSandbox.entities.set(id, ent);

			console.log("engine/network/entities - entity " + id + " created");
		}

		return offset - originalOffset;
	},

	UPDATEMASK_X: 2,
	UPDATEMASK_Y: 4,
	UPDATEMASK_ROTATION: 8,
	UPDATEMASK_HEALTH: 16,

	packetUpdatesEntUpdated: function(data, dataView, id, offset)
	{
		var originalOffset = offset;

		// Find entity
		ent = projectSandbox.entities.get(id);

		if (ent)
		{
			// Read mask
			var mask = dataView.getInt8(offset);
			offset += 1;

			// Read updated params
			if ((mask & this.UPDATEMASK_X) == this.UPDATEMASK_X)
			{
				ent.x = dataView.getFloat32(offset);
				offset += 4;
			}
			if ((mask & this.UPDATEMASK_Y) == this.UPDATEMASK_Y)
			{
				ent.y = dataView.getFloat32(offset);
				offset += 4;
			}
			if ((mask & this.UPDATEMASK_ROTATION) == this.UPDATEMASK_ROTATION)
			{
				ent.rotation = dataView.getFloat32(offset);
				offset += 4;
			}
			if ((mask & this.UPDATEMASK_HEALTH) == this.UPDATEMASK_HEALTH)
			{
				ent.health = dataView.getFloat32(offset);
				offset += 4;
			}

			// Set ent to alive
			ent.dead = false;

			// Allow ent to parse custom update bytes
			offset = ent.readBytes_update(data, dataView, id, offset);

			console.log("engine/network/entities - entity " + id + " updated");
		}
		else
		{
			console.warn("engine/network/entities - entity with id " + id + " not found for update");
		}

		return offset - originalOffset;
	},

	packetUpdatesEntDeleted: function(data, dataView, id, offset)
	{
		// Remove entity from the world
		var entity = projectSandbox.entities.get(id);
		if (entity != null)
		{
			// Remove from world
			projectSandbox.entities.delete(id);

            // Raise death event
			this.invokeEntityDeath(id, entity);

			console.log("engine/network/entities - entity " + id + " deleted");
		}
		else
		{
			console.warn("engine/network/entities - entity " + id + " not found for deletion");
		}
		return 0;
	},

	invokeEntityDeath: function(id, entity)
	{
	    // Invoke death event
        if (entity.eventDeath)
        {
            entity.eventDeath();
        }
	}
}
