package com.limpygnome.projectsandbox.server.constants.weapons;

/**
 * Created by limpygnome on 26/06/15.
 */
public class RocketConstants
{
    /**
     * The radius of affected entities from the explosion.
     */
    public static final float ROCKET_BLAST_RADIUS = 400.0f;

    /**
     * The linear damage applied from an Rocket blast; ranges from 0 to specified value, based on distance.
     */
    public static final float ROCKET_BLAST_DAMAGE = 1000.0f;

    /**
     * The spacing between an Rocket and the source which fired it.
     */
    public static final float ROCKET_LAUNCH_SPACING = 16.0f;

    /**
     * The lifespan of a rocket.
     */
    public static final float ROCKET_LIFESPAN_MS = 5000.0f;

    /**
     * The amount to increment the rocket speed.
     */
    public static final float ROCKET_SPEED_STEP = 0.2f;

    /**
     * The maximum speed of a rocket.
     */
    public static final float ROCKET_SPEED = 16.0f;

}
