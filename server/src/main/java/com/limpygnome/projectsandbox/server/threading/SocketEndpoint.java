package com.limpygnome.projectsandbox.server.threading;

import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * TODO: abstract websocket to allow switching of libs easily
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
    public void onMessage(WebSocket socket, String msg)
    {
        // We're only allowing binary, most likely user tampering with us...
        // TODO: debug logging
        socket.close();
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer message)
    {
        controller.packetManager.handleInbound(socket, message);
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
    public void onError(WebSocket socket, Exception e)
    {
        // Probably tampering...
        // TODO: debug logging
        socket.close();
    }
    
    public void broadcast(byte[] data)
    {
        for(WebSocket ws : connections())
        {
            ws.send(data);
        }
    }
    
}
