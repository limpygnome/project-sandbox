package com.projectsandbox.components.server.entity.ai;

/**
 * Used to track the current state of the pedestrian.
 */
public enum PedestrianState
{
    /**
     * Indicates the pedestrian is idle and returning to its spawn position.
     */
    IdleReturnToSpawn(true),

    /**
     * Indicates the pedestrian is idle and just aimlessly walking around the world.
     */
    IdleWalk(true),

    /**
     * Indicates the pedestrian is idle.
     */
    Idle(true),

    /**
     * Indicates the pedestrian is in pursuit and attacking an entity.
     */
    TrackingEntity(false)
    ;

    public final boolean IDLE;

    PedestrianState(boolean IDLE)
    {
        this.IDLE = IDLE;
    }
}