package com.limpygnome.projectsandbox.server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limpygnome.projectsandbox.server.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Basic implementation of a web socket server, using a third-party library. Implementation can be controlled and
 * switched by network service.
 */
class SocketEndpoint extends WebSocketServer
{
    private final static Logger LOG = LogManager.getLogger(SocketEndpoint.class);

    private Controller controller;
    
    public SocketEndpoint(Controller controller, int port) throws IOException
    {
        super(new InetSocketAddress(port));
        
        this.controller = controller;
    }

    @Override
    public void onMessage(WebSocket socket, String msg)
    {
        if (socket != null)
        {
            // We're only allowing binary, most likely user tampering with us...
            LOG.error("Non-binary received - ip: {}", socket.getRemoteSocketAddress());
            socket.close();
        }
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer message)
    {
        if (socket != null && message != null)
        {
            controller.packetService.handleInbound(socket, message);
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch)
    {
        if (ws != null)
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
                LOG.info("Client connected - ip: {}", ws.getRemoteSocketAddress());
        }
    }
    
    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln)
    {
        if (ws != null)
        {
            LOG.info("Client disconnected - ip: {}", ws.getRemoteSocketAddress());
            controller.playerService.unregister(ws);
        }
    }

    @Override
    public void onError(WebSocket socket, Exception e)
    {
        if (socket != null && e != null)
        {
            LOG.error("Socket exception, terminating - ip: {} - {}", socket != null ? socket.getRemoteSocketAddress() : "null", e);
            LOG.debug("Full stack trace from socket exception", e);

            socket.close();
        }
    }
    
}
