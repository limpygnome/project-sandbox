projectSandbox.network.player =
{
    packet: function(data, dataView, subType)
    {
        switch (subType)
        {
            // Identity
            case "I":
                this.packetPlayerIdentity(dataView);
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