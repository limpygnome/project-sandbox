package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;

/**
 * Used to inform components of when the PlayerInfo has changed for the enity.
 */
public interface PlayerInfoKeyDownComponentEvent
{

    /**
     *
     * @param playerInfo
     * @param key
     */
    void playerInfoKeyDown(PlayerInfo playerInfo, PlayerKeys key);

    /**
     *
     * @param playerInfo
     * @param key
     */
    void playerInfoKeyUp(PlayerInfo playerInfo, PlayerKeys key);

}
