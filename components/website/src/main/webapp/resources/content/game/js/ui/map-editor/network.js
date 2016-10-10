game.ui.mapEditor.network = (function(){

    var currentSpawns: null;

    var handlePacket = function(packet)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            case "L":
                handlePacketSpawnList(packet);
                break;
        }
    };

    var handlePacketSpawnList = function(packet)
    {
        // Fetch text and parse as JSON
        var json = packet.readAscii();
        var spawns = JSON.parse(json);

        // Remove any spawn-marker entities currently present
        if (currentSpawns != null)
        {
            for (var spawn In currentSpawns)
            {
            }
        }

        // Add spawn-marker entity for each spawn
        var entity;
        for (var spawn in spawns)
        {
            // Create util/spawn-marker (entity type: 902)
            entity = projectSandbox.network.entityPool.get(902);

            // Set position to match spawn
            entity.x = spawn.x;
            entity.y = spawn.y;
            entity.z = spawn.z;
            entity.rotation = spawn.rotation;

            // Add to world
            projectSandbox.entities.set(id, ent);
        }
    };

    var sendData = function(payload)
    {
        // Convert payload to string
        var data = JSON.stringify(payload);

        // Build new packet
        var packet = new projectSandbox.network.OutboundPacket();
        packet.addChar("M");
        packet.addChar("E");
        packet.addUtf8(data);

        // Send
        projectSandbox.network.send(packet.build());
    };

    return {
        handlePacket    : handlePacket,
        sendData        : sendData
    };

})();
