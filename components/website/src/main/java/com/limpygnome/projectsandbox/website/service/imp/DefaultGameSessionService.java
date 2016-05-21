package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.shared.jpa.repository.GameRepository;
import com.limpygnome.projectsandbox.shared.jpa.repository.result.CreateGameSessionResult;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultGameSessionService implements GameSessionService
{
    private final static Logger LOG = LogManager.getLogger(DefaultGameSessionService.class);

    @Autowired
    private GameRepository gameRepository;

    @Override
    public String fetchOrGenerateSessionToken(String nickname)
    {
        // Create new session
        // TODO: make sure registered users cant start with _guest
        GameSession gameSession = new GameSession("guest_" + nickname);
        CreateGameSessionResult gameSessionResult = gameRepository.createGameSession(gameSession);

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
    public String fetchOrGenerateSessionToken(User user)
    {
        // Attempt to find existing session
        String gameSessionToken = gameRepository.fetchExistingGameSessionToken(user);

        if (gameSessionToken == null)
        {
            // Create new session
            GameSession gameSession = new GameSession(user);
            CreateGameSessionResult createGameSessionResult = gameRepository.createGameSession(gameSession);

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
    public boolean validateExists(String gameSessionToken)
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
        boolean exists = gameRepository.isTokenValid(token.toString());
        return exists;
    }

}
