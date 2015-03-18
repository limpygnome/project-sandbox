package com.limpygnome.projectsandbox.threading;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.packets.InboundPacket;
import com.limpygnome.projectsandbox.packets.inbound.PlayerMovementPacket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author limpygnome
 */
public class SocketEndpoint extends WebSocketServer
{
    private Controller controller;
    
    public SocketEndpoint(Controller controller, int port) throws IOException
    {
        super(new InetSocketAddress(port));
        
        this.controller = controller;
    }

    @Override
    public void onMessage(WebSocket ws, String msg)
    {
        // Do nothing; all data expected to be binary...
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message)
    {
        byte[] data = message.array();
        byte mainType = data[0];
        byte subType = data[1];
        InboundPacket packet = null;
        
        // Create packet based on types
        switch(mainType)
        {
            case 'U':
                switch(subType)
                {
                    case 'M':
                        packet = new PlayerMovementPacket();
                        break;
                }
                break;
        }
        
        // Check we found a packet
        if(packet == null)
        {
            System.out.println("Unhandled message - type: '" + mainType + "', sub-type: '" + subType + "'");
            return;
        }
        
        // Parse data
        packet.parse(controller, conn, message, data);
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch)
    {
        System.out.println("Endpoint - client connected - " + ws.getRemoteSocketAddress());
        controller.playerManager.register(ws);
    }
    
    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln)
    {
        System.out.println("client disconnected - " + ws.getRemoteSocketAddress());
        controller.playerManager.unregister(ws);
    }

    @Override
    public void onError(WebSocket ws, Exception excptn)
    {
    }
    
    public void broadcast(byte[] data)
    {
        for(WebSocket ws : connections())
        {
            ws.send(data);
        }
    }
    
}
