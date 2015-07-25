package com.limpygnome.projectsandbox.server.packets.datatypes;

import java.nio.ByteBuffer;

/**
 * Represents a data type which can be sent via a packet.
 */
public interface AbstractDataType
{
    int getByteLength();

    void write(ByteBuffer buffer);
}
