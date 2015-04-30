package com.limpygnome.projectsandbox.server.packets.types.inventory;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

/**
 * Created by limpygnome on 29/04/15.
 */
public class InventoryItemSelectedInboundPacket extends InboundPacket
{
    @Override
    public void parse(Controller controller, WebSocket socket, ByteBuffer bb, byte[] data)
    {

    }
}
