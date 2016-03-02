/*
    Used to build outbound packets.
*/
projectSandbox.network.OutboundPacket = function()
{
    this.items = new Array();
    this.totalBytes = 0;
}

projectSandbox.network.OutboundPacket.prototype.addUuid = function(data)
{
    if (data.length != 36)
    {
        throw "Invalid UUID, only 36 char full form supported";
    }

    // Convert to bytes
    var bytes = new Uint8Array(36);
    for (var i = 0; i < 36; i++)
    {
        bytes[i] = data.charCodeAt(i);
    }

    // Add bytes
    this.items.push(bytes);
    this.totalBytes += 36;
}

projectSandbox.network.OutboundPacket.prototype.addChar = function(data)
{
    // Convert to bytes
    var bytes = new Uint8Array(1);
    bytes[0] = data.charCodeAt(0);

    // Add bytes
    this.items.push(bytes);
    this.totalBytes += 1;
}

projectSandbox.network.OutboundPacket.prototype.addByte = function(data)
{
    // Convert to bytes
    var bytes = new Uint8Array(1);
    var dataView = new DataView(bytes.buffer);
    dataView.setInt8(0, data);

    // Add bytes
    this.items.push(bytes);
    this.totalBytes += 1;
}

projectSandbox.network.OutboundPacket.prototype.build = function()
{
    // Build final array
    var finalBytes = new Uint8Array(this.totalBytes);
    var offsetBytes = 0;

    var item;
    for (var i = 0; i < this.items.length; i++)
    {
        item = this.items[i];

        finalBytes.set(item, offsetBytes);
        offsetBytes += item.length;
    }

    return finalBytes;
}
