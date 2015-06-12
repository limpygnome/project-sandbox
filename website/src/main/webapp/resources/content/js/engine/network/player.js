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

    PLAYER_KILLED_MASK_PLAYERID_KILLER: 1,

    packetPlayerKilled: function(data, dataView)
    {
        var flags = dataView.getInt8(2);

        var causeText = projectSandbox.utils.parseText(data, dataView, 3);
        var causeTextLenOffset = 4 + causeText.length;

        var entityIdVictim = dataView.getInt16(causeTextLenOffset);
        var entityIdKiller = dataView.getInt16(causeTextLenOffset + 2);
        var playerIdVictim = dataView.getInt16(causeTextLenOffset + 4);
        var playerIdKiller;

        if ((flags & this.PLAYER_KILLED_MASK_PLAYERID_KILLER) == this.PLAYER_KILLED_MASK_PLAYERID_KILLER)
        {
            playerIdKiller = dataView.getInt16(2 + causeText.length + 6);
        }
        else
        {
            playerIdKiller = null;
        }

        // Inform UI via hook
        projectSandbox.game.ui.hookPlayer_entKilled(causeText, entityIdVictim, entityIdKiller, playerIdVictim, playerIdKiller);
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
                    offset = this.packetPlayerEvents_joined(data, dataView, offset);
                    break;
                case "U":
                    offset = this.packetPlayerEvents_updates(data, dataView, offset);
                    break;
                case "L":
                    offset = this.packetPlayerEvents_left(data, dataView, offset);
                    break;
                default:
                    console.error("engine/network/player - unhandled event type: " + eventType);
                    console.error(data);
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

        // Check player does not already exist
        if (!projectSandbox.players.contains(playerId))
        {
            // Create model and add to players table
            var player = new projectSandbox.types.player(playerId, displayName);
            projectSandbox.players.add(player);

            // Invoke UI hook
            projectSandbox.game.ui.hook_playerJoined(player);

            console.debug("engine/network/player - player joined - ply id: " + playerId + ", name: " + displayName);
        }
        else
        {
            console.debug("engine/network/player - ignoring duplicate player join event - ply id: " + playerId);
        }

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

        var score = dataView.getUint32(offset);
        offset += 4;

        // Update player's metrics
        var player = projectSandbox.players.get(playerId);
        player.kills = kills;
        player.deaths = deaths;
        player.score = score;

        // Invoke UI hook
        projectSandbox.game.ui.hook_playerUpdated(player);

        console.debug("engine/network/player - updated player - ply id: " + playerId + ", kills: " + kills + ", deaths: " + deaths + ", score: " + score);

        return offset;
    },

    packetPlayerEvents_left: function(data, dataView, offset)
    {
        // Parse data
        var playerId = dataView.getInt16(offset);
        offset += 2;

        // Fetch player
        var player = projectSandbox.players.get(playerId);

        if (player != null)
        {
            // Remove player from player table
            projectSandbox.players.remove(playerId);

            // Invoke UI hook
            projectSandbox.game.ui.hook_playerLeft(player);
        }
        else
        {
            console.debug("engine/network/player - player left, but not found - ply id: " + playerId);
        }

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