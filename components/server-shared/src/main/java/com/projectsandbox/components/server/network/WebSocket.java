package com.projectsandbox.components.server.network;

import java.net.InetSocketAddress;

/**
 * Created by limpygnome on 18/05/16.
 */
public class WebSocket implements Socket
{
    private org.java_websocket.WebSocket webSocket;

    public WebSocket(org.java_websocket.WebSocket webSocket)
    {
        this.webSocket = webSocket;
    }

    @Override
    public void send(byte[] data)
    {
        webSocket.send(data);
    }

    @Override
    public boolean isOpen()
    {
        return webSocket.isOpen();
    }

    @Override
    public void close()
    {
        webSocket.close();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress()
    {
        return webSocket.getRemoteSocketAddress();
    }

}
