/*
    Wrapper class/function to read packet data, which internally stores the data and offset/bytes-read.

    TODO:
    - drop text reading functions from utils
    - replace all of network classes/functions to use this
*/

projectSandbox.network.InboundPacket = function(data, bytesRead)
{
    this.bytesRead = bytesRead;
    this.data = data;
    this.dataView = new DataView(data.buffer);
}

projectSandbox.network.InboundPacket.prototype.hasMoreData = function()
{
    return this.bytesRead < this.data.length;
}

projectSandbox.network.InboundPacket.prototype.readByte = function()
{
    var value = this.dataView.getInt8(this.bytesRead);
    this.bytesRead += 1;
    return value;
}

projectSandbox.network.InboundPacket.prototype.readChar = function()
{
    var value = String.fromCharCode(this.data[this.bytesRead]);
    this.bytesRead += 1;
    return value;
}

projectSandbox.network.InboundPacket.prototype.readShort = function()
{
    var value = this.dataView.getInt16(this.bytesRead);
    this.bytesRead += 2;
    return value;
}

projectSandbox.network.InboundPacket.prototype.readInt = function()
{
    var value = this.dataView.getInt32(this.bytesRead);
    this.bytesRead += 4;
    return value;
}

/*
    Note: a long is considered 32-bits, despite Java using 64-bits, since this is the typical standard/expectation.
*/
projectSandbox.network.InboundPacket.prototype.readLong = function()
{
    var value = this.dataView.getUint32(this.bytesRead);
    this.bytesRead += 4;
    return value;
}

projectSandbox.network.InboundPacket.prototype.readFloat = function()
{
    var value = this.dataView.getFloat32(this.bytesRead);
    this.bytesRead += 4;
    return value;
}


projectSandbox.network.InboundPacket.prototype.readAscii = function()
{
    // Fetch length/total bytes for text
    var length = this.dataView.getInt8(this.bytesRead);

    // Fetch text
    var text = String.fromCharCode.apply(
        String,
        this.data.subarray(this.bytesRead + 1, this.bytesRead + 1 + length)
    );

    // Increment bytes read
    this.bytesRead += 1 + length;

    return text;
}

projectSandbox.network.InboundPacket.prototype.readUtf8 = function()
{
    // Fetch length/total bytes for text
    var length = this.dataView.getInt16(this.bytesRead);

    // Fetch text
    var text = String.fromCharCode.apply(
        String,
        this.data.subarray(this.bytesRead + 2, this.bytesRead + 2 + length)
    );

    // Increment bytes read
    this.bytesRead += 2 + length;

    return text;
}
