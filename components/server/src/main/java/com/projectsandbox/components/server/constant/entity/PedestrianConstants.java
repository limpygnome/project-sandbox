package com.projectsandbox.components.server.constant.entity;

import com.projectsandbox.components.game.weapon.GatlingItem;

/**
 * Created by limpygnome on 04/09/15.
 */
public final class PedestrianConstants
{

    private PedestrianConstants() { }

    public static final short ENT_WIDTH = 16;

    public static final short ENT_HEIGHT = 9;

    /**
     * When the target's angle offset is within this range, the abstract pedestrian will proceed to check for a
     * collision before shooting. This pretty much avoids having to cast a bullet, saving resources.
     */
    public static final float ROTATIONAL_OFFSET_TO_ATTACK = 0.26f;



    public static final float ATTACKING_PED_HEALTH = 80.0f;

    public static final Class[] ATTACKING_PED_INVENTORY =
    {
        GatlingItem.class
    };

    public static final float ATTACKING_PED_ENGAGE_DISTANCE = 150.0f;

    public static final float ATTACKING_PED_FOLLOW_SPEED = 1.8f;

    public static final float ATTACKING_PED_FOLLOW_DISTANCE = 450.0f;

    public static final float ATTACKING_PED_ATTACK_DISTANCE = 200.0f;

    /**
     * Amount of rotation, at max, to randomly add to the pedestrian's rotation to decrease the accuracy of their
     * weapon/firing.
     */
    public static final float ATTACKING_PED_ATTACK_ROTATION_NOISE = 0.4f;

}
