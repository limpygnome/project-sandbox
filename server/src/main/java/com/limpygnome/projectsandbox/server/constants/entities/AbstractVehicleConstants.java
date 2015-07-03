package com.limpygnome.projectsandbox.server.constants.entities;

/**
 * Created by limpygnome on 03/07/15.
 */
public class AbstractVehicleConstants
{
    /**
     * The minimum speed to apply damage in a collision.
     */
    public static final float MINIMUM_SPEED_DAMAGE = 2.0f;

    /**
     * The ratio of damage between 0.0 to 1.0 shared equally between the cars. The rest of the damage is applied to
     * the slowest entity..
     */
    public static final float EQUAL_DAMAGE_RATIO = 0.1f;

    /**
     * The multiplier used with the speed to determine the total damage caused.
     */
    public static final float COLLISION_SPEED_MULTIPLIER = 80.0f;
}
