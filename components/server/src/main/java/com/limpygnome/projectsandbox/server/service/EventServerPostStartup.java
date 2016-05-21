package com.limpygnome.projectsandbox.server.service;

import com.limpygnome.projectsandbox.server.Controller;

/**
 * Invoked after the server has started up.
 */
public interface EventServerPostStartup
{

    /**
     * Invoked on post server startup.
     *
     * @param controller the controller
     */
    void eventServerPostStartup(Controller controller);

}
