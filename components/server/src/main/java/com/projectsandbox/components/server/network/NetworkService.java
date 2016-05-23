package com.projectsandbox.components.server.network;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.player.PlayerService;
import com.projectsandbox.components.server.service.EventServerPostStartup;
import com.projectsandbox.components.server.service.EventServerShutdown;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Used as a wrapper around a socket endpoint to handle inbound data to the game server, from clients.
 */
@Service
public class NetworkService implements EventServerPostStartup, EventServerShutdown
{
    private final static Logger LOG = LogManager.getLogger(PlayerService.class);

    private SocketEndpoint socketEndpoint;

    @Override
    public void eventServerPostStartup(Controller controller)
    {
        LOG.info("Starting network service...");

        try
        {
            socketEndpoint = new SocketEndpoint(controller, 4857);
            socketEndpoint.start();

            LOG.info("Started network endpoint - port {}", socketEndpoint.getPort());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to startup network service", e);
        }
    }

    @Override
    public void eventServerShutdown(Controller controller)
    {
        try
        {
            socketEndpoint.stop();
        }
        catch (Exception e)
        {
            LOG.warn("Failed to stop network endpoint", e);
        }
    }

    public SocketEndpoint getSocketEndpoint()
    {
        return socketEndpoint;
    }

}
