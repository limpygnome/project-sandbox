package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.jpa.models.User;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultGameSessionService implements GameSessionService
{

    @Override
    public String generateSessionToken(String nickname)
    {
        return generateToken();
    }

    @Override
    public String generateSessionToken(User user)
    {
        // Attempt to find a previous session token
        return generateToken();
    }

    private String generateToken()
    {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean validateAndConsume(String gameSessionToken)
    {
        // Validate this token is correct; attacks will probably be made against this code
        if (gameSessionToken == null)
        {
            return false;
        }

        gameSessionToken = gameSessionToken.trim();

        // Attempt to parse as UUID
        try
        {
            UUID.fromString(gameSessionToken);
        }
        catch (Exception e)
        {
            return false;
        }

        // Check the session exists in the DB

        return true;
    }
}
