package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

/**
 * Used to inform components of when the PlayerInfo has changed for the enity.
 */
public interface PlayerInfoKeyDownComponentEvent extends ComponentEvent
{

    /**
     * Used to raise when a key has changed state.
     *
     * @param controller controller
     * @param playerInfo the player which has made the key change
     * @param key the key affected
     * @param index the seat of the player, starting from zero
     * @param isKeyDown indicates the new state of whether the key is down (or not)
     */
    void eventPlayerInfoKeyChange(Controller controller, PlayerInfo playerInfo, PlayerKeys key, int index, boolean isKeyDown);

}
