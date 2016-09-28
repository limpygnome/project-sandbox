package com.projectsandbox.components.server.player;

/**
 * Created by limpygnome on 28/09/16.
 */
public interface PlayerKeysObserver
{

    /**
     * Invoked when a key for a player is pressed down.
     *
     * @param key the key
     */
    void keyDown(PlayerKeys key);

    /**
     * Invoked when a key for a player is no longer pressed down.
     *
     * @param key the key
     */
    void keyUp(PlayerKeys key);

}
