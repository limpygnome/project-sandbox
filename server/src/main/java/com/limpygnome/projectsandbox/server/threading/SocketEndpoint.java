package com.limpygnome.projectsandbox.server.threading;

import com.limpygnome.projectsandbox.server.packets.inbound.PlayerMovementPacket;
import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.packets.InboundPacket;
import com.limpygnome.projectsandbox.server.packets.inbound.SessionIdentifierPacket;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;
import com.limpygnome.projectsandbox.server.players.Session;
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
    public void onMessage(WebSocket ws, ByteBuffer message)
    {
        byte[] data = message.array();
        byte mainType = data[0];
        byte subType = data[1];
        
        // Fetch the player's info
        PlayerInfo playerInfo = controller.playerManager.getPlayerByWebSocket(ws);

        // Check if we're expecting a session packet - always first packet to system!
        if (playerInfo == null)
        {
            // Check we have received session packet
            if (mainType == 'U' && subType == 'S')
            {
                // Parse packet and load session associated with player
                SessionIdentifierPacket sessPacket = new SessionIdentifierPacket();
                sessPacket.parse(controller, ws, message, data);
                
                // Check data / socket valid
                if (sessPacket.sessionId != null && ws.isOpen())
                {
                    // Load session data from database
                    // TODO: actually load from DB
                    Session session = new Session();

                    // Log event
                    System.out.println("Session " + session.sessionId + " <> " + ws.getRemoteSocketAddress());
                    
                    // Register player
                    controller.playerManager.register(ws, session);
                    return;
                }
            }
            
            // Some other packet / invalid data / no session
            // TODO: add debug msg; nothing else. could be attack...
            ws.close();
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
        packet.parse(controller, ws, message, data);
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch)
    {
        /*
        TODO: have a hashmap which rejects clients who've made too many
        invalid connections e.g. invalid session data etc. This would help
        protect against denial of service attacks, people trying to write hacks
        etc. Have a thread which goes through all the sockets, automatically
        kills and adds a counter against any connections who fail to auth within
        5s - add new conns to a list, remove when auth'd. If a user auths,
        remove entries for them. Could still perform DOS by spamming 5, open
        one. Going to be fun to protect against attacks.
        */
        System.out.println("Endpoint - client connected - " + ws.getRemoteSocketAddress());
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
