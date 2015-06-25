projectSandbox.network =
{
	webSocket: null,

	closed: true,
	
	setup: function()
	{
	    var host = projectSandbox.assetLoader.get("/content/game/settings.json")["host"];
	    console.debug("engine/network - host: '" + host + "'");

		// Create socket
		webSocket = new WebSocket("ws://" + host);
		webSocket.binaryType = 'arraybuffer';

		// Hook events
		var self = projectSandbox.network;

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
		console.log("engine/network - connection established");

		this.closed = false;

		// Invoke UI hook
		projectSandbox.game.ui.hookSocket_connected();

		// Send session ID - must always be done first
		this.player.sendSessionIdPacket();
	},

	wsEventError: function(event)
	{
		if (!this.closed)
		{
			console.error("engine/network - error - " + event);
		}
	},
	
	wsEventClose: function(event)
	{
		if (!this.closed)
		{
			console.log("engine/network - socket closed");
			this.closed = true;

			// Reset world
			projectSandbox.reset();

			// Invoke UI hook
			projectSandbox.game.ui.hookSocket_disconnected();
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
