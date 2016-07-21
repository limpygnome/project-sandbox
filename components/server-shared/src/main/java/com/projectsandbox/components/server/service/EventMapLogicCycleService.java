package com.projectsandbox.components.server.service;

import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Implemented by services requiring logic to be executed for every map during each logic cycle.
 */
public interface EventMapLogicCycleService
{

    /**
     * Executes logic for a map during a logic cycle.
     */
    void logic(WorldMap map);

}
