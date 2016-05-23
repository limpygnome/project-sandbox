package com.projectsandbox.components.server.service;

import com.projectsandbox.components.server.Controller;

/**
 * Implemented by services which require a startup event/method to be invoked on server startup.
 */
public interface EventServerPreStartup
{

    /**
     * Invoked when game logic first starts, before the first actual logic cycle.
     */
    void eventServerPreStartup(Controller controller);

}
