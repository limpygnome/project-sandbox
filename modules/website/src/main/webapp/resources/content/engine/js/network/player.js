projectSandbox.network.player =
{
    /*
        Inbound
        ----------------------------------------------------------------------------------------------------------------
    */
    handlePacket: function(packet)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            // Identity
            case "I":
                this.packetPlayerIdentity(packet);
                return;

            // Killed
            case "K":
                this.packetPlayerKilled(packet);
                return;

            // Events/Updates
            case "E":
                this.packetPlayerEvents(packet);
                return;

            // Chat message
            case "C":
                this.packetPlayerChatMessage(packet);
                return;

            default:
                console.error("engine/network/player - unknown sub-type - " + subType);
                break;
        }
    },

    packetPlayerIdentity: function(packet)
    {
        var playerId = packet.readShort();
        var entityId = packet.readShort();

        // Update our player ID
        projectSandbox.playerId = playerId;

        // Update our entity ID
        projectSandbox.playerEntityId = entityId;

        // Update camera chase
        projectSandbox.camera.chaseEntityId = entityId;

        // Reset UI
        projectSandbox.game.ui.hookPlayer_entChanged();

        console.debug("engine/network/player - updated player - player ID: " + playerId + ", entity ID: " + entityId);
    },

    PLAYER_KILLED_MASK_PLAYERID_KILLER: 1,

    packetPlayerKilled: function(packet)
    {
        var flags = packet.readByte();
        var causeText = packet.readAscii();
        var entityIdVictim = packet.readShort();
        var entityIdKiller = packet.readShort();
        var playerIdVictim = packet.readShort();
        var playerIdKiller;

        if ((flags & this.PLAYER_KILLED_MASK_PLAYERID_KILLER) == this.PLAYER_KILLED_MASK_PLAYERID_KILLER)
        {
            playerIdKiller = packet.readShort();
        }
        else
        {
            playerIdKiller = null;
        }

        // Inform UI via hook
        projectSandbox.game.ui.hookPlayer_entKilled(causeText, entityIdVictim, entityIdKiller, playerIdVictim, playerIdKiller);
    },

    packetPlayerEvents: function(packet)
    {
        var eventType;

        while (packet.hasMoreData())
        {
            eventType = packet.readChar();

            switch (eventType)
            {
                case "J":
                    offset = this.packetPlayerEvents_joined(packet);
                    break;
                case "U":
                    offset = this.packetPlayerEvents_updates(packet);
                    break;
                case "L":
                    offset = this.packetPlayerEvents_left(packet);
                    break;
                default:
                    console.error("engine/network/player - unhandled event type: " + eventType);
                    break;
            }
        }
    },

    packetPlayerEvents_joined: function(packet)
    {
        // Parse data
        var playerId = packet.readShort();
        var displayName = packet.readUtf8();

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
    },

    packetPlayerEvents_updates: function(packet)
    {
        // Parse data
        var playerId = packet.readShort();
        var kills = packet.readShort();
        var deaths = packet.readShort();
        var score = packet.readLong();

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

    packetPlayerEvents_left: function(packet)
    {
        // Parse data
        var playerId = packet.readShort();

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
    },

    packetPlayerChatMessage: function(packet)
    {
        var playerId = packet.readShort();
        var nickname = packet.readUtf8();
        var message = packet.readUtf8();

        // Fetch player
        var player = projectSandbox.players.get(playerId);

        if (player != null)
        {
            // Invoke UI to handle message
            projectSandbox.game.ui.hook_playerChatMessage(player, nickname, message);

            console.debug("engine/player - chat message - player id: " + playerId, ", nickname: " + nickname + ", msg: " + message);
        }
        else
        {
            console.warn("engine/player - received chat message for non-existent player - player id: " + playerId);
        }
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
    },

    sendChatMessage: function(message)
    {
        // Convert message to bytes
        var messageBytes = projectSandbox.utils.str2bytes(message);

        // Build packet
        var packet = new Uint8Array(4 + messageBytes.length);
        var packetDataView = new DataView(packet.buffer);

        packet[0] = "P".charCodeAt(0);
        packet[1] = "C".charCodeAt(0);

        packetDataView.setInt16(2, messageBytes.length);
        projectSandbox.utils.copy2array(packet, 4, messageBytes);

        // Send packet
        projectSandbox.network.send(packet.buffer);

        console.debug("engine/network/players - sending chat message: " + message);
    }

}