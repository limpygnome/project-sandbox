package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.shared.model.User;

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
    String generateSessionToken(String nickname);

    String generateSessionToken(User user);

    /**
     * Validates the game session token exists and consumes it.
     *
     * @param gameSessionToken The token
     * @return True = valid and consumed, false = invalid session token
     */
    boolean validateAndConsume(String gameSessionToken);
}
