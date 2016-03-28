projectSandbox.network.world.map =
{
    handlePacket: function(packet)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            // Tile data map
            case "T":
                projectSandbox.network.world.mapTiles.handlePacket(packet);
                return;

            // Open map
            case "O":
                projectSandbox.network.world.mapOpen.handlePacket(packet);
                return;

            default:
                console.error("engine/network/map - unknown sub-type - " + subType);
                return;
        }
    }

}
