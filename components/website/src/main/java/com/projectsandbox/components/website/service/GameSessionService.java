package com.projectsandbox.components.website.service;

import com.projectsandbox.components.shared.model.User;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface GameSessionService
{
    /**
     * Generates a session token.
     *
     * @param nickname
     * @return A session token, or null if failed.
     */
    String fetchOrGenerateSessionToken(String nickname);

    String fetchOrGenerateSessionToken(User user);

    /**
     * Validates the game session token exists.
     *
     * @param gameSessionToken The token
     * @return True = valid, false = invalid session token
     */
    boolean validateExists(String gameSessionToken);
}
