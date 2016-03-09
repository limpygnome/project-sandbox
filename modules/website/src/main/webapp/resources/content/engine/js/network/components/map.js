projectSandbox.network.map =
{
    handlePacket: function(packet)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            // Read map data
            case "D":
                this.packetMapData(packet);
                return;

            default:
                console.error("engine/network/map - unknown sub-type - " + subType);
                return;
        }
    },

    packetMapData: function(packet)
    {
        // Reset map
        projectSandbox.map.reset();

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
        projectSandbox.map.tileSize = tileSize;
        projectSandbox.map.width = width;
        projectSandbox.map.height = height;

        // Parse tiles
        var type;

        for(y = height - 1; y >=0 ; y--)
        {
            projectSandbox.map.tiles[y] = [];

            for(x = 0; x < width; x++)
            {
                type = packet.readShort();
                projectSandbox.map.tiles[y][x] = type;
            }
        }

        // Set map to setup
        projectSandbox.map.setup();
    },

    packetMapDataTileType: function(packet)
    {
        var id = packet.readShort();
        var height = packet.readShort();
        var textureName = packet.readAscii();

        // Fetch texture
        var texture = projectSandbox.textures.get(textureName);

        if (texture == null)
        {
            texture = projectSandbox.textures.get("error");
            console.error("engine/network/map - failed to load tile type texture - id : " + id + ", texture name: '" + textureName + "'");
        }

        // Set tile type
        projectSandbox.map.types[id] = [texture, height];
    }

}
