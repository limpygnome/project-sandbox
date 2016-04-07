package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.service.LogicService;
import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.jpa.provider.UserProvider;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.PlayerMetrics;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 26/07/15.
 */
@Service
public class SessionService implements LogicService
{
    private final static Logger LOG = LogManager.getLogger(SessionService.class);

    private static final int INTERVAL_UPDATE_SESSION = 60;

    private UserProvider userProvider;
    private GameProvider gameProvider;

    private List<GameSession> trackedGameSessions;

    public SessionService()
    {
        this.userProvider = new UserProvider();
        this.gameProvider = new GameProvider();
        this.trackedGameSessions = new LinkedList<>();

        try
        {
            gameProvider.begin();
            gameProvider.setAllSessionsToDisconnected();
            gameProvider.commit();
        }
        catch (Exception e)
        {
            LOG.error("Failed to set all pre-existing connected sessions to disconnected", e);

            gameProvider.rollback();
        }
    }

    public synchronized GameSession load(UUID token)
    {
        if (gameProvider != null)
        {
            GameSession gameSession = gameProvider.fetchGameSessionByToken(token);

            // Track session for periodic DB updates
            if (gameSession != null)
            {
                trackedGameSessions.add(gameSession);
            }

            return gameSession;
        }
        else
        {
            LOG.error("Unable to load game session, provider not set - token: {}", token);

            return null;
        }
    }

    public synchronized void unload(GameSession gameSession)
    {
        // Set session to unconnected
        gameSession.getPlayerMetrics().setLastUpdatedNow();
        gameSession.setConnected(false);

        // Transfer score/kills/death to user's profile/account
        User user = gameSession.getUser();

        if (user != null)
        {
            PlayerMetrics gameSessionMetrics = gameSession.getPlayerMetrics();
            PlayerMetrics userMetrics = user.getPlayerMetrics();

            // Transfer metrics
            userMetrics.transferFromGameSession(gameSessionMetrics);

            persist(gameSession, user);
        }
        else
        {
            persist(gameSession);
        }

        // Remove from tracked list
        trackedGameSessions.remove(gameSession);
    }

    public synchronized void persist(GameSession gameSession)
    {
        persist(gameSession, null);
    }

    private synchronized void persist(GameSession gameSession, User user)
    {
        // Check valid game session passed
        if (gameSession == null)
        {
            throw new IllegalArgumentException("Invalid/null game session provided");
        }

        // Check valid providers available
        if (gameProvider != null && (user == null || userProvider != null))
        {
            // Persist session
            try
            {
                gameProvider.begin();
                gameProvider.updateGameSession(gameSession);
                gameProvider.commit();
            }
            catch (Exception e)
            {
                LOG.error("Failed to persist game session", e);

                gameProvider.rollback();
                return;
            }

            // Persist user, if specified
            try
            {
                if (user != null)
                {
                    userProvider.begin();
                    userProvider.updateUser(user);
                    userProvider.commit();
                }
            }
            catch (Exception e)
            {
                LOG.error("Failed to persist user", e);

                userProvider.rollback();
                return;
            }

            LOG.debug("Persisted game session - token: {}", gameSession.getToken());
        }
        else
        {
            LOG.error("Unable to persist game session, provider(s) not setup - token: {}", gameSession.getToken());
        }
    }

    @Override
    public synchronized void logic()
    {
        try
        {
            gameProvider.begin();

            // Persist all tracked sessions
            int secondsSinceUpdated;
            for (GameSession gameSession : trackedGameSessions)
            {
                secondsSinceUpdated = Seconds.secondsBetween(
                        gameSession.getPlayerMetrics().getLastUpdated(), DateTime.now()).getSeconds();

                if (gameSession.getPlayerMetrics().isDirtyDatabaseFlag() || secondsSinceUpdated >= INTERVAL_UPDATE_SESSION)
                {
                    gameSession.getPlayerMetrics().setLastUpdatedNow();
                    gameProvider.updateGameSession(gameSession);
                }
            }

            gameProvider.commit();
        }
        catch (Exception e)
        {
            LOG.error("Failed to perform periodic game session sync", e);

            gameProvider.rollback();
        }
    }

    // TODO: call on shutdown
    public synchronized void dispose()
    {
        if (gameProvider != null)
        {
            gameProvider.close();
            gameProvider = null;

            LOG.info("Disposed game provider");
        }

        if (userProvider != null)
        {
            userProvider.close();
            userProvider = null;

            LOG.info("Disposed user provider");
        }
    }

}
