package com.limpygnome.projectsandbox.server.service;

/**
 * Implemented by services requiring custom logic to be executed each game logic cycle.
 */
public interface LogicService
{

    /**
     * Executes a logic cycle for a service.
     */
    void logic();

}
