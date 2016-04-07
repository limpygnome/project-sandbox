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
     *
     * @param playerInfo the player
     */
    PlayerEntity createPlayer(PlayerInfo playerInfo);

    /**
     * Persists the player's current entity to their session.
     *
     * @param playerInfo the player
     */
    void persistPlayer(PlayerInfo playerInfo);

}
