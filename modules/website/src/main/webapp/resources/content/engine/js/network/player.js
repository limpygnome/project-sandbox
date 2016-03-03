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
        // -- THey may have left game and this may not be found, but it's not critical...
        var player = projectSandbox.players.get(playerId);

        // Invoke UI to handle message
        projectSandbox.game.ui.hook_playerChatMessage(player, nickname, message);
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
        var packet = new projectSandbox.network.OutboundPacket();
        packet.addChar("P");
        packet.addChar("S");
        packet.addUuid(projectSandbox.sessionId);

        // Send packet
        projectSandbox.network.send(packet.build());
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
            var packet = new projectSandbox.network.OutboundPacket();
            packet.addChar("P");
            packet.addChar("M");
            packet.addShort(projectSandbox.playerEntityId);
            packet.addShort(movement);

            // Send packet
            projectSandbox.network.send(packet.build());
        }
    },

    sendChatMessage: function(message)
    {
        var packet = new projectSandbox.network.OutboundPacket();
        packet.addChar("P");
        packet.addChar("C");
        packet.addUtf8(message);

        // Send packet
        projectSandbox.network.send(packet.build());

        console.debug("engine/network/players - sending chat message: " + message);
    }

}