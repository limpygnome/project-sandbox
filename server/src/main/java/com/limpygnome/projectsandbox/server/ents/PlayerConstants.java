package com.limpygnome.projectsandbox.server.ents;

import com.limpygnome.projectsandbox.server.inventory.items.Fist;
import com.limpygnome.projectsandbox.server.inventory.items.weapons.Smg;

/**
 *
 * @author limpygnome
 */
public interface PlayerConstants
{
    float DEFAULT_HEALTH = 100.0f;
    Class[] DEFAULT_INVENTORY_ITEMS = {
            Smg.class,
            Fist.class
    };
}
