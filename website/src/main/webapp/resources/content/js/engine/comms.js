projectSandbox.comms =
{
	webSocket: null,

	closed: true,
	
	setup: function()
	{
		// Create socket
		webSocket = new WebSocket("ws://localhost:4857");
		webSocket.binaryType = 'arraybuffer';

		// Hook events
		var self = projectSandbox.comms;
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

		this.closed = false;
		
		// Send session ID - must always be done first
		projectSandbox.commsPacket.sendSessionId();
	},

	wsEventError: function(event)
	{
		if (!this.closed)
		{
			console.error("Comms - error - " + event);
		}
	},
	
	wsEventClose: function(event)
	{
		if (!this.closed)
		{
			console.log("Comms - socket closed");
			this.closed = true;

			// Reset world
			projectSandbox.reset();
		}

		// Attempt to reconnect
		setTimeout(this.setup, 1000);
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
			// Entities
			case "E":
				projectSandbox.network.entities.packet(data, dataView, subType);
				return;

            // Maps
			case "M":
				projectSandbox.network.map.packet(data, dataView, subType);
				return;

            // Players
			case "P":
				projectSandbox.network.player.packet(data, dataView, subType);
				return;

            // Inventory
			case "I":
				projectSandbox.network.inventory.packet(subType, data);
				return;

            // Effects
			case "Z":
				projectSandbox.game.effects.packet(data, dataView, subType);
				return;
		}
		
		console.error("engine/network - unhandled message - type: " + mainType + ", sub-type: " + subType);
	}
}
