package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.website.model.User;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface GameSessionService
{
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
