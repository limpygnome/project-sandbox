package com.limpygnome.projectsandbox.server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Basic implementation of a web socket server, using a third-party library. Implementation can be controlled and
 * switched by network service.
 */
public class SocketEndpoint extends WebSocketServer
{
    private final static Logger LOG = LogManager.getLogger(SocketEndpoint.class);

    private HashMap<WebSocket, Socket> internalToApiSocketMapper;
    private Controller controller;
    
    public SocketEndpoint(Controller controller, int port) throws IOException
    {
        super(new InetSocketAddress(port));
        
        this.controller = controller;
        this.internalToApiSocketMapper = new HashMap<>();
    }

    @Override
    public void onMessage(WebSocket webSocket, String msg)
    {
        Socket socket = getSocketMapping(webSocket);

        if (socket != null)
        {
            // We're only allowing binary, most likely user tampering with us...
            LOG.error("non-binary received - ip: {}", socket.getRemoteSocketAddress());
            socket.close();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer message)
    {
        Socket socket = getSocketMapping(webSocket);

        if (socket != null && message != null)
        {
            controller.packetService.handleInbound(socket, message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake ch)
    {
        Socket socket = getSocketMapping(webSocket);

        if (socket != null)
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
                LOG.info("client connected - ip: {}", webSocket.getRemoteSocketAddress());
        }
    }
    
    @Override
    public void onClose(WebSocket webSocket, int i, String string, boolean bln)
    {
        Socket socket = getSocketMapping(webSocket);

        if (socket != null)
        {
            LOG.info("client disconnected - ip: {}", webSocket.getRemoteSocketAddress());

            // Unregister player
            controller.playerService.unregister(socket);

            // Remove mapping
            internalToApiSocketMapper.remove(webSocket);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e)
    {
        Socket socket = getSocketMapping(webSocket);

        if (socket != null && e != null)
        {
            LOG.error("socket exception, terminating - ip: {} - {}", socket != null ? socket.getRemoteSocketAddress() : "null", e);
            LOG.debug("full stack trace from socket exception", e);
        }

        // Best to kill the socket...
        webSocket.close();
        LOG.warn("connection closed - socket error - remote host: {}", webSocket.getRemoteSocketAddress());
    }

    private Socket getSocketMapping(WebSocket webSocket)
    {
        Socket socket = internalToApiSocketMapper.get(webSocket);

        if (socket == null)
        {
            // Disconnect the socket, since we can't do anything with it...
            webSocket.close();

            LOG.debug("connection closed - could not find mapping for web-socket - remote host: {}", webSocket.getRemoteSocketAddress());
        }

        return socket;
    }
    
}
