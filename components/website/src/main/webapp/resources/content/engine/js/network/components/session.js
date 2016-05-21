projectSandbox.network.session =
{

    handlePacket: function(packet, subType)
    {
        var subType = packet.readChar();

        switch (subType)
        {
            // Error code
            case "E":
                this.packetErrorCode(packet);
                return;

            default:
                console.error("engine/network/session - unknown sub-type - " + subType);
                break;
        }
    },

    packetErrorCode: function(packet)
    {
        // Parse the error code
        var errorCode = packet.readInt();

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
        projectSandbox.game.ui.controller.hookSession_errorCode(errorCode);
    }

}
