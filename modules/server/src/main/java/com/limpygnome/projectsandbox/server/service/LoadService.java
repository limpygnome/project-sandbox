package com.limpygnome.projectsandbox.server.service;

/**
 * Implemented by services which require a load method  to be invoked before logic cycles.
 */
public interface LoadService
{

    /**
     * Invoked when game logic first starts, before the first actual logic cycle.
     */
    void load();

}
