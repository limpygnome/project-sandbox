package com.projectsandbox.components.server.service;

import com.projectsandbox.components.server.Controller;

/**
 * Implemented by services which need an event/method invoked on server shutdown.
 */
public interface EventServerShutdown
{

    /**
     * Invoked when the server shuts down.
     */
    void eventServerShutdown(Controller controller);

}
