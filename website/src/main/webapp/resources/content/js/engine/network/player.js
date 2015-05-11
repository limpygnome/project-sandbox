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

        console.log("Comms - updated player id to " + id);
    }
}