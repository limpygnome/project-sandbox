package com.projectsandbox.components.server.network;

import java.net.InetSocketAddress;

/**
 * Created by limpygnome on 18/05/16.
 */
public interface Socket
{

    void send(byte[] data);

    boolean isOpen();

    void close();

    InetSocketAddress getRemoteSocketAddress();

}
