package com.projectsandbox.components.server.network.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Thrown when a packet cannot be parsed.
 */
public class PacketHandlerException extends Exception
{
    private final static Logger LOG = LogManager.getLogger(PacketHandlerException.class);

    public PacketHandlerException(String message, byte[] data)
    {
        super(message);

        // We'll log the contents of the packet in debug mode for the purposes of development
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Failed to either parse or handle packet; contents: {}", Arrays.toString(data));
        }
    }

}
