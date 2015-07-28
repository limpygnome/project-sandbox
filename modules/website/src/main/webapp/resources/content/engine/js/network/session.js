projectSandbox.network.session =
{

    packet: function(data, dataView, subType)
    {
        switch (subType)
        {
            // Error code
            case "E":
                this.packetErrorCode(data, dataView);
                return;

            default:
                console.error("engine/network/session - unknown sub-type - " + subType);
                break;
        }
    },

    packetErrorCode: function(data, dataView)
    {
        // Parse the error code
        var errorCode = dataView.getInt32(2);

        // Handle error code
        switch (errorCode)
        {
            case 1:
                console.error("engine/network/session - error code - session not found");
                break;

            default:
                console.error("engine/network/session - unknown error code: " + errorCode);
                break;
        }

        // Disable networking
        projectSandbox.network.disabled = true;

        // Invoke UI hook
        projectSandbox.game.ui.hookSession_errorCode(errorCode);
    }

}
