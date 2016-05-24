package com.projectsandbox.components.server.constant.weapon;

/**
 * Created by limpygnome on 26/06/15.
 */
public class RocketConstants
{
    /**
     * The radius of affected entities from the explosion.
     */
    public static final float ROCKET_BLAST_RADIUS = 120.0f;

    /**
     * The linear damage applied from an Rocket blast; ranges from 0 to specified value, based on distance.
     */
    public static final float ROCKET_BLAST_DAMAGE = 600.0f;

    /**
     * The spacing between an Rocket and the source which fired it.
     */
    public static final float ROCKET_LAUNCH_SPACING = 0.0f;

    /**
     * The lifespan of a rocket.
     */
    public static final float ROCKET_LIFESPAN_MS = 30000.0f;

    /**
     * The amount to increment the rocket speed.
     */
    public static final float ROCKET_SPEED_STEP = 1.5f;

    /**
     * The maximum speed of a rocket.
     */
    public static final float ROCKET_SPEED = 40.0f;

}
