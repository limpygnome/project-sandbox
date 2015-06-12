package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.inventory.items.Fist;
import com.limpygnome.projectsandbox.server.inventory.items.weapons.Smg;

/**
 *
 * @author limpygnome
 */
public interface PlayerConstants
{

    /**
     * Default player health.
     */
    float DEFAULT_HEALTH = 100.0f;

    /**
     * Default inventory for players.
     */
    Class[] DEFAULT_INVENTORY_ITEMS = {
            Smg.class,
            Fist.class
    };

    /**
     * The default distance a player will travel each logic cycle.
     */
    float DEFAULT_MOVEMENT_SPEED_FACTOR = 2.0f;

    /**
     * The default rotation a player can rotate each logic cycle.
     */
    float DEFAULT_ROTATION_FACTOR = 0.15f;

}
