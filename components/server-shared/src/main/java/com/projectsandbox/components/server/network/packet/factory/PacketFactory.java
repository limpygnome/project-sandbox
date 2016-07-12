package com.projectsandbox.components.server.network.packet.factory;

import com.projectsandbox.components.server.network.packet.handler.InboundPacketHandler;
import com.projectsandbox.components.server.player.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to get the appropriate packet.
 */
@Component
public class PacketFactory
{
    private final static Logger LOG = LogManager.getLogger(PacketFactory.class);

    // Store main/sub type as integer, which maps to packet type
    private Map<Long, InboundPacketHandler> packetHandlers;

    public PacketFactory()
    {
        packetHandlers = new HashMap<>();
    }

    @Autowired
    public void setupHandlers(List<InboundPacketHandler> inboundPacketHandlers)
    {
        Class<? extends InboundPacketHandler> handlerClass;
        PacketHandler handlerAnnotation;
        long key;

        for (InboundPacketHandler handler : inboundPacketHandlers)
        {
            // Fetch annotation to read main/sub type
            handlerClass = handler.getClass();
            handlerAnnotation = handlerClass.getAnnotation(PacketHandler.class);

            // Add to map
            key = convert(handlerAnnotation.mainType(), handlerAnnotation.subType());
            packetHandlers.put(key, handler);
        }

        LOG.debug("setup packet factory - {} handlers available", packetHandlers.size());
    }

    public InboundPacketHandler getInboundPacket(PlayerInfo playerInfo, byte mainType, byte subType)
    {
        // Fetch class
        long key = convert(mainType, subType);
        InboundPacketHandler handler = packetHandlers.get(key);
        return handler;
    }

    public long convert(byte mainType, byte subType)
    {
        long value = ((long) mainType << 8) | (long) subType;
        return value;
    }

}
