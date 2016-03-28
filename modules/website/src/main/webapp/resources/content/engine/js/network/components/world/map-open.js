projectSandbox.network.world.mapOpen =
{

    handlePacket: function(packet)
    {
        // Create new map
        projectSandbox.map = new projectSandbox.world.MapOpen();

        // Parse map limits (x and y co-ordinates)
        var limitWidth = packet.readFloat();
        var limitHeight = packet.readFloat();

        projectSandbox.map.limitWidth = limitWidth;
        projectSandbox.map.limitHeight = limitHeight;

        // Read boolean to check if background present
        var isBackground = packet.readBool();

        if (isBackground)
        {
            // Parse background
            var background = packet.readAscii();
            projectSandbox.map.background = background;
        }

        // Invoke map setup
        projectSandbox.map.setup();
    }

};
