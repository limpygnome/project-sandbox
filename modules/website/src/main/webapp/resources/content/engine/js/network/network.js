projectSandbox.network =
{
    webSocket: null,

    closed: true,
    disabled: false,
    
    setup: function()
    {
        var host = projectSandbox.assetLoader.get("/content/game/settings.json")["host"];
        console.debug("engine/network - host: '" + host + "', connecting...");

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

        if (!this.disabled)
        {
            // Attempt to reconnect
            setTimeout(this.setup, 1000);
        }
    },
    
    wsEventMessage: function(event)
    {
        var data = new Uint8Array(event.data);

        // Create packet
        var packet = new projectSandbox.network.InboundPacket(data, 0);

        var mainType = packet.readChar();

        switch (mainType)
        {
            // Entities
            case "E":
                projectSandbox.network.entities.handlePacket(packet);
                return;

            // Maps
            case "M":
                projectSandbox.network.map.handlePacket(packet);
                return;

            // Players
            case "P":
                projectSandbox.network.player.handlePacket(packet);
                return;

            // Inventory
            case "I":
                projectSandbox.network.inventory.handlePacket(packet);
                return;

            // Effects
            case "Z":
                projectSandbox.game.effects.handlePacket(packet);
                return;

            // Sessions
            case "S":
                projectSandbox.network.session.handlePacket(packet);
                return;
        }
        
        console.error("engine/network - unhandled message - type: " + mainType + ", sub-type: " + subType);
    }

}
