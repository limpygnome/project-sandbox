projectSandbox.network.world.mapTiles =
{

    handlePacket: function(packet)
    {
        // Create new map
        projectSandbox.map = new projectSandbox.world.MapTiles();

        // Parse number of tile types
        var numTileTypes = packet.readShort();

        // Parse tile types
        for(i = 0; i < numTileTypes; i++)
        {
            this.packetMapDataTileType(packet);
        }

        // Parse attributes
        // TODO: change to UUID...
        var mapId = packet.readShort();
        var tileSize = packet.readShort();
        var width = packet.readShort();
        var height = packet.readShort();

        // Setup map
        projectSandbox.world.map.tileSize = tileSize;
        projectSandbox.world.map.width = width;
        projectSandbox.world.map.height = height;

        // Parse tiles
        var type;

        for(y = height - 1; y >=0 ; y--)
        {
            projectSandbox.world.map.tiles[y] = [];

            for(x = 0; x < width; x++)
            {
                type = packet.readShort();
                projectSandbox.world.map.tiles[y][x] = type;
            }
        }

        // Set map to setup
        projectSandbox.world.map.setup();
    }

};
