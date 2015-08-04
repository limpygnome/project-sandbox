package com.limpygnome.projectsandbox.server.constant;

import com.limpygnome.projectsandbox.server.inventory.item.Fist;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.RocketLauncher;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.Smg;
import com.limpygnome.projectsandbox.server.inventory.item.weapon.SuicideVest;

/**
 *
 * @author limpygnome
 */
public class PlayerConstants
{

    /**
     * Default player health.
     */
    public static final float DEFAULT_HEALTH = 100.0f;

    /**
     * Default inventory for players.
     */
    public static final Class[] DEFAULT_INVENTORY_ITEMS = {
            Fist.class,
            Smg.class,
            RocketLauncher.class,
            SuicideVest.class
    };

    /**
     * The default distance a player will travel each logic cycle.
     */
    public static final float DEFAULT_MOVEMENT_SPEED_FACTOR = 2.0f;

    /**
     * The default rotation a player can rotate each logic cycle.
     */
    public static final float DEFAULT_ROTATION_FACTOR = 0.15f;

    /**
     * The default time to wait before respawning a player.
     */
    public static final long DEFAULT_RESPAWN_TIME_MS = 5000;

}