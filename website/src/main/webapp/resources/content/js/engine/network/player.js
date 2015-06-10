projectSandbox.network.player =
{
    /*
        Inbound
        ----------------------------------------------------------------------------------------------------------------
    */
    packet: function(data, dataView, subType)
    {
        switch (subType)
        {
            // Identity
            case "I":
                this.packetPlayerIdentity(dataView);
                return;

            // Killed
            case "K":
                this.packetPlayerKilled(data, dataView);
                return;

            // Events/Updates
            case "E":
                this.packetPlayerEvents(data, dataView);
                return;

            default:
                console.error("engine/network/player - unknown sub-type - " + subType);
                break;
        }
    },

    packetPlayerIdentity: function(dataView)
    {
        var id = dataView.getInt16(2);

        // Update our playerid
        projectSandbox.playerEntityId = id;

        // Update camera chase
        projectSandbox.camera.chaseEntityId = id;

        // Reset UI
        projectSandbox.game.ui.hookPlayer_entChanged();

        console.log("engine/network/player - updated player id to " + id);
    },

    packetPlayerKilled: function(data, dataView)
    {
        // Parse packet
        var id = dataView.getInt16(2);
        var causeTextLength = dataView.getInt8(4);
        var causeText = String.fromCharCode.apply(String, data.subarray(5, 5 + causeTextLength));

        // Check if we were killed
        if (id == projectSandbox.playerEntityId)
        {
            projectSandbox.game.ui.hookPlayer_entKilled(causeText);
        }
    },

    packetPlayerEvents: function(data, dataView)
    {
        var offset = 2; // main/sub type bytes

        var eventType;
        while (offset < data.length)
        {
            eventType = String.fromCharCode(dataView.getInt8(offset));
            offset++;

            switch (eventType)
            {
                case "J":
                    offset += this.packetPlayerEvents_joined(data, dataView, offset);
                    break;
                case "U":
                    offset += this.packetPlayerEvents_updates(data, dataView, offset);
                    break;
                case "L":
                    offset += this.packetPlayerEvents_left(data, dataView, offset);
                    break;
            }
        }
    },

    packetPlayerEvents_joined: function(data, dataView, offset)
    {
        // Parse data
        var playerId = dataView.getInt16(offset);
        offset += 2;

        var displayName = projectSandbox.utils.parseText(data, dataView, offset);
        offset += displayName.length + 1;

        // Add player to player table


        console.debug("engine/network/player - player joined - ply id: " + playerId + ", name: " + displayName);

        return offset;
    },

    packetPlayerEvents_updates: function(data, dataView, offset)
    {
        // Parse data
        var playerId = dataView.getInt16(offset);
        offset += 2;

        var kills = dataView.getInt16(offset);
        offset += 2;

        var deaths = dataView.getInt16(offset);
        offset += 2;

        var score = data.getInt64(offset);
        offset += 8;

        // Update player's metrics

        console.debug("engine/network/player - updated player - ply id: " + playerId + ", kills: " + kills + ", deaths: " + deaths + ", score: " + score);

        return offset;
    },

    packetPlayerEvents_left: function(data, dataView, offset)
    {
        // Parse data
        var playerId = dataView.getInt16(offset);
        offset += 2;

        // Remove player from player table

        console.debug("engine/network/player - player left game - ply id: " + playerId);

        return offset;
    },


    /*
        Outbound
        ----------------------------------------------------------------------------------------------------------------
    */

    // Movement constants
    MOVEMENT_UP: 1,
    MOVEMENT_LEFT: 2,
    MOVEMENT_DOWN: 4,
    MOVEMENT_RIGHT: 8,

    ACTION_KEY: 16,

    SPACEBAR: 32768,

    // Previous packet - no point updating the server if the value is the same!
    previousMovement: 0,

    sendSessionIdPacket: function()
    {
        var buff = new Uint8Array(38);

        // Header data
        buff[0] = "P".charCodeAt(0); // U
        buff[1] = "S".charCodeAt(0); // S

        // Session ID / UUID
        for(var i = 0; i < 36; i++)
        {
            buff[2+i] = projectSandbox.sessionId.charCodeAt(i);
        }

        // Send packet
        projectSandbox.network.send(buff.buffer);
    },

    sendUpdateMovementPacket: function()
    {
        // Compute new movement packet
        var movement = 0;

        if(projectSandbox.keyboard.isKeyDown("W"))
        {
            movement |= this.MOVEMENT_UP;
        }
        if(projectSandbox.keyboard.isKeyDown("S"))
        {
            movement |= this.MOVEMENT_DOWN;
        }
        if(projectSandbox.keyboard.isKeyDown("A"))
        {
            movement |= this.MOVEMENT_LEFT;
        }
        if(projectSandbox.keyboard.isKeyDown("D"))
        {
            movement |= this.MOVEMENT_RIGHT;
        }

        if (projectSandbox.keyboard.isKeyDown("F"))
        {
            movement |= this.ACTION_KEY;
        }

        if (projectSandbox.keyboard.isKeyDown(" "))
        {
            movement |= this.SPACEBAR;
        }

        // Compare and decide if to send
        if(movement != this.previousMovement)
        {
            // Update state
            this.previousMovement = movement;

            // Build packet
            var buff = new Uint8Array(6);
            var dv = new DataView(buff.buffer);
            // -- Header data
            buff[0] = "P".charCodeAt(0);
            buff[1] = "M".charCodeAt(0);
            // -- Entity ID
            dv.setInt16(2, projectSandbox.playerEntityId);
            // -- Movement flags
            dv.setInt16(4, movement);

            // Send packet
            projectSandbox.network.send(buff.buffer);
        }
    }
}