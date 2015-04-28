package com.limpygnome.projectsandbox.website.packets;

import com.limpygnome.projectsandbox.website.Controller;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public abstract class InboundPacket extends Packet
{
    public InboundPacket(byte mainType, byte subType)
    {
        super(mainType, subType);
    }
    
    public abstract void parse(Controller controller, WebSocket ws, ByteBuffer bb, byte[] data);
}
