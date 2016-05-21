package com.limpygnome.projectsandbox.server.testing;

import com.limpygnome.projectsandbox.server.network.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * A mock implementation of an underlying socket for a player.
 */
public class MockSocket implements Socket
{
    private final static Logger LOG = LogManager.getLogger(MockSocket.class);

    private boolean open;

    public MockSocket()
    {
        this.open = true;
    }

    @Override
    public void send(byte[] data)
    {
        // Nothing at present...
    }

    @Override
    public boolean isOpen()
    {
        return open;
    }

    @Override
    public void close()
    {
        open = false;
        LOG.info("socket closed");
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress()
    {
        return new InetSocketAddress("localhost-mock", 1234);
    }

}
