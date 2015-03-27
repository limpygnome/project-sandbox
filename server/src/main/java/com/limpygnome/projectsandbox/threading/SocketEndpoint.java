package com.limpygnome.projectsandbox.threading;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.packets.InboundPacket;
import com.limpygnome.projectsandbox.packets.inbound.PlayerMovementPacket;
import com.limpygnome.projectsandbox.packets.inbound.SessionIdentifierPacket;
import com.limpygnome.projectsandbox.players.PlayerInfo;
import com.limpygnome.projectsandbox.players.Session;
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
        
        // Fetch the player's info
        PlayerInfo playerInfo = controller.playerManager.getPlayerByWebSocket(conn);
        if (playerInfo == null)
        {
            // TODO: race condition could occur where onMessage before register; test against it. 
            conn.close();
            return;
        }
        
        // Check if we're expecting a session packet - always first packet to system!
        if (playerInfo.session == null)
        {
            // Check we have received session packet
            if (mainType == 'U' && subType == 'S')
            {
                // Parse packet and load session associated with player
                SessionIdentifierPacket sessPacket = new SessionIdentifierPacket();
                sessPacket.parse(controller, conn, message, data);
                
                // Check data / socket valid
                if (sessPacket.sessionId != null && conn.isOpen())
                {
                    // Load session data from database
                    // TODO: actually load from DB
                    Session session = new Session();
                    
                    playerInfo.session = session;
                    return;
                }
            }
            
            // Some other packet / invalid data / no session
            // TODO: add debug msg; nothing else. could be attack...
            conn.close();
            return;
        }
        
        // Create packet based on types
        InboundPacket packet = null;
        
        switch(mainType)
        {
            case 'U':
                switch(subType)
                {
                    case 'M':
                        // Player movement/update packet
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
