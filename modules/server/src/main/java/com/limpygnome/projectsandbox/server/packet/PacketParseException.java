package com.limpygnome.projectsandbox.server.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Thrown when a packet cannot be parsed.
 */
public class PacketParseException extends Exception
{
    private final static Logger LOG = LogManager.getLogger(PacketParseException.class);

    public PacketParseException(String message, byte[] data)
    {
        super(message);

        // We'll log the contents of the packet in debug mode for the purposes of development
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Failed to parse packet; contents: {}", Arrays.toString(data));
        }
    }
}
