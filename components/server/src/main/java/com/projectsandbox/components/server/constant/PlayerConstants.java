package com.projectsandbox.components.server.constant;

import com.projectsandbox.components.game.weapon.Fist;
import com.projectsandbox.components.game.weapon.rocket.RocketLauncherItem;
import com.projectsandbox.components.game.weapon.GatlingItem;
import com.projectsandbox.components.game.weapon.SuicideVestItem;

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
            GatlingItem.class,
            RocketLauncherItem.class,
            SuicideVestItem.class
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

    /**
     * The width of the player.
     */
    public static final short PLAYER_WIDTH = 16;

    /**
     * The height of the player.
     */
    public static final short PLAYER_HEIGHT = 9;

}
