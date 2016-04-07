package com.limpygnome.projectsandbox.server.service;

import com.limpygnome.projectsandbox.server.Controller;

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
