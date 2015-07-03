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
    public static final float ENT_EQUAL_DAMAGE_RATIO = 0.1f;

    /**
     * The multiplier used with the speed to determine the total damage caused.
     */
    public static final float ENT_COLLISION_SPEED_DAMAGE_MULTIPLIER = 80.0f;

    /**
     * The multiplier applied to a vehicle's speed when it collides with the map.
     */
    public static final float MAP_COLLISION_SPEED_MULTIPLIER = 0.8f;

    /**
     * The multiplier used with the speed to determine the total damage caused.
     */
    public static final float MAP_COLLISION_SPEED_DAMAGE_MULTIPLIER = 1.0f;

}
