package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateGameSessionResult;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultGameSessionService implements GameSessionService
{
    private final static Logger LOG = LogManager.getLogger(DefaultGameSessionService.class);

    @Override
    public String fetchOrGenerateSessionToken(GameProvider gameProvider, String nickname)
    {
        gameProvider.begin();

        // Create new session
        // TODO: make sure registered users cant start with _guest
        GameSession gameSession = new GameSession("guest_" + nickname);
        CreateGameSessionResult gameSessionResult = gameProvider.createGameSession(gameSession);

        gameProvider.commit();

        if (gameSessionResult != CreateGameSessionResult.SUCCESS)
        {
            return null;
        }
        else
        {
            return gameSession.getToken().toString();
        }
    }

    @Override
    public String fetchOrGenerateSessionToken(GameProvider gameProvider, User user)
    {
        // Attempt to find existing session
        String gameSessionToken = gameProvider.fetchExistingGameSessionToken(user);

        if (gameSessionToken == null)
        {
            gameProvider.begin();

            // Create new session
            GameSession gameSession = new GameSession(user);
            CreateGameSessionResult createGameSessionResult = gameProvider.createGameSession(gameSession);

            gameProvider.commit();

            // Check new session could be created
            if (createGameSessionResult != CreateGameSessionResult.SUCCESS)
            {
                return null;
            }

            gameSessionToken = gameSession.getToken();
        }

        return gameSessionToken;
    }

    @Override
    public boolean validateExists(GameProvider gameProvider, String gameSessionToken)
    {
        // Validate this token is correct; attacks will probably be made against this code
        if (gameSessionToken == null)
        {
            LOG.debug("Null token for validate and consume");
            return false;
        }

        gameSessionToken = gameSessionToken.trim();

        // Attempt to parse as UUID
        UUID token;

        try
        {
            token = UUID.fromString(gameSessionToken);
        }
        catch (Exception e)
        {
            LOG.debug("Unable to validate game session token - token: {}", gameSessionToken);
            return false;
        }

        // Check the session exists in the DB
        boolean exists = gameProvider.isTokenValid(token.toString());
        return exists;
    }
}
