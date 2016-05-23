package com.projectsandbox.components.server.entity.ai;

/**
 * Specifies what a pedestrian should do when idle.
 */
public enum IdleMode
{
    /**
     * Causes the pedestrian to walk around the world.
     */
    WALK,

    /**
     * Causes the pedestrian to return to their spawn position.
     */
    RETURN_TO_SPAWN
}