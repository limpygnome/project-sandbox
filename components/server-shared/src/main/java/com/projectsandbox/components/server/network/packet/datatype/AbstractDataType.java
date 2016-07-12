package com.projectsandbox.components.server.network.packet.datatype;

import java.nio.ByteBuffer;

/**
 * Represents a data type which can be sent via a packet.
 */
public interface AbstractDataType
{
    int getByteLength();

    void write(ByteBuffer buffer);
}
