package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Created by limpygnome on 26/07/15.
 */
public class SessionManager
{
    private final static Logger LOG = LogManager.getLogger(SessionManager.class);
    private GameProvider gameProvider;

    public SessionManager()
    {
        this.gameProvider = new GameProvider();
    }

    public synchronized GameSession load(UUID token)
    {
        if (gameProvider != null)
        {
            GameSession gameSession = gameProvider.fetchGameSessionByToken(token);

            return gameSession;
        }
        else
        {
            LOG.error("Unable to load game session, provider not set - token: {}", token);

            return null;
        }
    }

    public synchronized void persist(GameSession gameSession)
    {
        if (gameProvider != null)
        {
            gameProvider.begin();
            gameProvider.updateGameSession(gameSession);
            gameProvider.commit();

            LOG.debug("Persisted game session - token: {}", gameSession.getToken());
        }
        else
        {
            LOG.error("Unable to persist game session, provider not set - token: {}", gameSession.getToken());
        }
    }

    // TODO: call on shutdown
    public synchronized void dispose()
    {
        if (gameProvider != null)
        {
            gameProvider.close();
            gameProvider = null;

            LOG.info("Disposed database provider");
        }
    }

}
