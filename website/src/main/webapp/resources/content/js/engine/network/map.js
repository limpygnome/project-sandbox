projectSandbox.network.map =
{
    packet: function(data, dataView, subType)
    {
        switch (subType)
        {
            case "D":
                this.packetMapData(data);
                return;
            default:
                console.error("engine/network/map - unknown sub-type - " + subType);
                return;
        }
    },

    packetMapData: function(data)
    {
        var dataView = new DataView(data.buffer);
        var offset = 2;

        // Reset map
        projectSandbox.map.reset();

        // Parse number of tile types
        var numTileTypes = dataView.getInt16(offset);
        offset += 2;

        // Parse tile types
        for(i = 0; i < numTileTypes; i++)
        {
            offset += this.packetMapDataTileType(data, dataView, offset);
        }

        // Parse attributes
        var mapId = dataView.getInt16(offset);
        var tileSize = dataView.getInt16(offset + 2);
        var width = dataView.getInt16(offset + 4);
        var height = dataView.getInt16(offset + 6);
        offset += 8;

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
                type = dataView.getInt16(offset);
                projectSandbox.map.tiles[y][x] = type;
                offset += 2;
            }
        }

        // Set map to setup
        projectSandbox.map.setup();
    },

    packetMapDataTileType: function(data, dataView, offset)
    {
        var originalOffset = offset;

        var id = dataView.getInt16(offset);
        offset += 2;

        var height = dataView.getInt16(offset);
        offset += 2;

        var textureNameLen = dataView.getInt8(offset);
        var textureName = String.fromCharCode.apply(String, data.subarray(offset + 1, offset + 1 + textureNameLen));
        offset += 1 + textureNameLen;

        // Fetch texture
        var texture = projectSandbox.textures.get(textureName);

        if (texture == undefined || texture == null)
        {
            texture = projectSandbox.textures.get("error");
            console.error("engine/network/map - failed to load tile type texture - id : " + id + ", texture name: '" + textureName + "'");
        }

        // Set tile type
        projectSandbox.map.types[id] = [texture, height];

        return offset - originalOffset;
    }
}
