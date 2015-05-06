projectSandbox.comms =
{
	webSocket: null,
	
	setup: function()
	{
		// Create socket
		webSocket = new WebSocket("ws://localhost:4857");
		webSocket.binaryType = 'arraybuffer';
		
		// Hook events
		var self = this;
		webSocket.onopen = function(event)
		{
			self.wsEventOpen(event);
		};
		webSocket.onclose = function(event)
		{
			self.wsEventClose(event);
		};
		webSocket.onmessage = function(event)
		{
			self.wsEventMessage(event);
		};
		webSocket.onerror = function(event)
		{
			self.wsEventError(event);
		};
	},
	
	send: function(data)
	{
		if(webSocket != null)
		{
			webSocket.send(data);
		}
	},
	
	wsEventOpen: function(event)
	{
		console.log("Comms - connection established");
		
		// Send session ID - must always be done first
		projectSandbox.commsPacket.sendSessionId();
	},
	
	wsEventClose: function(event)
	{
		console.log("Comms - socket closed");
		
		// Reset world
		projectSandbox.reset();
		
		// Attempt to reconnect
		this.setup();
	},
	
	wsEventMessage: function(event)
	{
		var data = new Uint8Array(event.data);
		var dataView = new DataView(data.buffer);
		
		var mainType = String.fromCharCode(data[0]);
		var subType = String.fromCharCode(data[1]);

		//console.debug("Received packet - mt: " + mainType + ", st: " + subType);
		//console.debug(data);

		switch (mainType)
		{
			case "E": // Entities
				switch (subType)
				{
					case "U":
						this.packetEntityUpdates(data);
						return;
				}
				break;
			case "M": // Maps
				switch (subType)
				{
					case "D":
						this.packetMapData(data);
						return;
				}
				break;
			case "P": // Players
				switch (subType)
				{
					case "I":
						this.packetPlayerIdentity(data);
						return;
				}
				break;
			case "I": // Inventory
				projectSandbox.inventory.packetInventory(subType, data);
				return;
			case "Z":
				projectSandbox.game.effects.packet(data, dataView, subType);
				return;
		}
		
		console.error("Comms - unhandled message - type: " + mainType + ", sub-type: " + subType);
	},
	
	packetMapData: function(data)
	{
		var dataView = new DataView(data.buffer);
		var offset = 2;
		
		// Reset map
		projectSandbox.map.reset();
		
		// Parse number of tile types
		var numTileTypes = dataView.getInt16(offset);
		offset += 2;
		
		// Parse tile types
		for(i = 0; i < numTileTypes; i++)
		{
			offset += this.packetMapDataTileType(data, dataView, offset);
		}
		
		// Parse attributes
		var mapId = dataView.getInt16(offset);
		var tileSize = dataView.getInt16(offset + 2);
		var width = dataView.getInt16(offset + 4);
		var height = dataView.getInt16(offset + 6);
		offset += 8;
		
		// Setup map
		projectSandbox.map.tileSize = tileSize;
		projectSandbox.map.width = width;
		projectSandbox.map.height = height;

		// Parse tiles
		var type;
		for(y = height - 1; y >=0 ; y--)
		{
			projectSandbox.map.tiles[y] = [];
			for(x = 0; x < width; x++)
			{
				type = dataView.getInt16(offset);
				projectSandbox.map.tiles[y][x] = type;
				offset += 2;
			}
		}
		
		// Set map to setup
		projectSandbox.map.setup();
	},
	
	packetMapDataTileType: function(data, dataView, offset)
	{
		var originalOffset = offset;
		
		var id = dataView.getInt16(offset);
		offset += 2;
		
		var height = dataView.getInt16(offset);
		offset += 2;
		
        var textureNameLen = dataView.getInt8(offset);
        var textureName = String.fromCharCode.apply(String, data.subarray(offset + 1, offset + 1 + textureNameLen));
        offset += 1 + textureNameLen;
		
        // Fetch texture
        var texture = projectSandbox.textures.get(textureName);
        
        if (texture == undefined || texture == null)
        {
            texture = projectSandbox.textures.get("error");
            console.error("Comms - failed to load tile type texture - id : " + id + ", texture name: '" + textureName + "'");
        }
        
        // Set tile type
		projectSandbox.map.types[id] = [texture, height];
		
		return offset - originalOffset;
	},
	
	packetTextureData: function(data)
	{
		var dv = new DataView(data.buffer);
		var offset = 2;
		
		// Parse texture src's
		var numSrc = dv.getInt16(offset);
		offset += 2;
		for(i = 0; i < numSrc; i++)
		{
			offset += this.packetTextureDataParseSrc(data, dv, offset);
		}
		
		// Parse textures
		var numTextures = dv.getInt16(offset);
		offset += 2;
		for(i = 0; i < numTextures; i++)
		{
			offset += this.packetTextureDataParseTexture(data, dv, offset);
		}
	},
	
	packetTextureDataParseSrc: function(data, dv, offset)
	{
		// Parse data
		var id = dv.getInt16(offset);
		var urlLen = dv.getInt16(offset + 2);
		var url = String.fromCharCode.apply(String, data.subarray(offset + 4, offset + 4 + urlLen));
		
		// Construct instance
		var textureSrc = new TextureSrc(id, url);
		
		// Add to map
		projectSandbox.texturesSrc.set(id, textureSrc);
		console.log("Comms - texture src added - srcId: " + id + ", url: " + url);
		
		return 2 + 2 + urlLen;
	},
	
	packetTextureDataParseTexture: function(data, dv, offset)
	{
		// Parse data
		var id = dv.getInt16(offset);
		var srcId = dv.getInt16(offset + 2);
		var speed = dv.getInt16(offset + 4);
		var frames = dv.getInt16(offset + 6);
		
		// Parse frame data
		offset += 8;
		
		// -- frames * 8 vertices * 4 bytes each
		var frameVerts = frames * 8;
		var frameData = new Float32Array(frames*frameVerts);
		for(frameVert = 0; frameVert < frameVerts; frameVert++)
		{
			frameData[frameVert] = dv.getFloat32(offset);
			offset += 4;
		}
		
		// Construct instance
		var texture = new Texture(id, srcId, speed, frames, frameData);
		
		// Add to map
		projectSandbox.textures.set(id, texture);
		console.log("Comms - texture added - id: " + id + ", srcId: " + srcId);
		
		return 2 + 2 + 2 + 2 + (frameVerts * 4);
	},
	
	packetPlayerIdentity: function(data)
	{
		var dv = new DataView(data.buffer);
		var id = dv.getInt16(2);
		
		// Update our playerid
		projectSandbox.playerEntityId = id;
		
		// Update camera chase
		projectSandbox.camera.chaseEntityId = id;

		// Reset UI
		projectSandbox.game.ui.hookPlayer_entChanged();
		
		console.log("Comms - updated player id to " + id);
	},
	
	packetEntityUpdates : function(data)
	{
		// Create dataview to extract data
		var dataView = new DataView(data.buffer);
		
		var offset = 2; // maintype / subtype = 2 bytes
		
		var updateType;
		var id;
		var bytesRead;
		
		while (offset < data.length)
		{
			// Retrieve update type and entity id
			updateType = dataView.getInt8(offset);
			id = dataView.getInt16(offset + 1);
			
			// Increment offset
			offset += 3; // 1 byte for update type, 2 bytes for id
			
			// Handle update based on type
			switch(updateType)
			{
				case 67: // C 
					bytesRead = this.packetEntityUpdatesEntCreated(data, dataView, id, offset);
					break;
				case 85: // U 
					bytesRead = this.packetEntityUpdatesEntUpdated(data, dataView, id, offset);
					break;
				case 68: // D 
					bytesRead = this.packetEntityUpdatesEntDeleted(data, dataView, id, offset);
					break;
				default:
					console.log("Comms - unknown entity update type '" + updateType + "'");
					break;
			}
			
			// Increment offset
			offset += bytesRead; // 1 byte for update type, 2 bytes for ent id
		}
	},
	
	packetEntityUpdatesEntCreated : function(data, dataView, id, offset)
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
				console.warn("Comms - unhandled ent type " + entityType);
				break;
			case 0:
				ent = new Entity();
				break;
			case 1:
				ent = new Player();
				break;
			case 20:
				ent = new IceCreamVan();
				break;
		}
		
		if (ent != null)
		{
			// Set max health
			ent.maxHealth = maxHealth;
			
			// TODO: read custom byte data here
			
			// Add to world
			projectSandbox.entities.set(id, ent);
			
			console.log("Comms - entity " + id + " created");
		}
		
		return offset - originalOffset;
	},
	
	UPDATEMASK_X: 1,
	UPDATEMASK_Y: 2,
	UPDATEMASK_ROTATION: 4,
	UPDATEMASK_HEALTH: 8,
	
	packetEntityUpdatesEntUpdated : function(data, dataView, id, offset)
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
			
			// Allow ent to parse custom update bytes
			offset = ent.readBytes_update(data, dataView, id, offset);
			
			console.log("Comms - entity " + id + " updated");
		}
		else
		{
			console.warn("Comms - entity with id " + id + " not found for update");
		}
		
		return offset - originalOffset;
	},
	
	packetEntityUpdatesEntDeleted : function(data, dataView, id, offset)
	{
		// Remove entity from the world
		projectSandbox.entities.delete(id);
		console.log("Comms - entity " + id + " deleted");
		
		return 0;
	},

	wsEventError: function(event)
	{
		console.error("Comms - error - " + event);
	}
}
