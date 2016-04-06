package com.limpygnome.projectsandbox.server.entity.factory;

import com.limpygnome.projectsandbox.server.entity.PlayerEntity;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

/**
 * Used to create entities.
 */
public interface EntityFactory
{

    /**
     * Creates a new entity for a player.
     */
    PlayerEntity createPlayer(PlayerInfo playerInfo);

    /**
     * needs thought...
     *
     * @param playerInfo
     */
    void persistPlayer(PlayerInfo playerInfo);

}
