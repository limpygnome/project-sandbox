game.ui.mapEditor.network = (function(){

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
        sendData        : sendData
    };

})();
