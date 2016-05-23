package com.projectsandbox.components.server.player;

import com.projectsandbox.components.shared.jpa.repository.GameRepository;
import com.projectsandbox.components.shared.jpa.repository.UserRepository;
import com.projectsandbox.components.shared.model.GameSession;
import com.projectsandbox.components.shared.model.PlayerMetrics;
import com.projectsandbox.components.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Used to manage session data for players.
 */
@Service
public class SessionService
{
    private final static Logger LOG = LogManager.getLogger(SessionService.class);

    private static final int INTERVAL_UPDATE_SESSION = 60;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    private List<GameSession> trackedGameSessions;

    public SessionService()
    {
        this.trackedGameSessions = new LinkedList<>();
    }

    @PostConstruct
    public void setup()
    {
        try
        {
            gameRepository.setAllSessionsToDisconnected();
        }
        catch (Exception e)
        {
            LOG.error("Failed to set all pre-existing connected sessions to disconnected", e);
        }
    }

    public synchronized GameSession load(UUID token)
    {
        if (gameRepository != null)
        {
            GameSession gameSession = gameRepository.fetchGameSessionByToken(token);

            // Track session for periodic DB updates
            if (gameSession != null)
            {
                trackedGameSessions.add(gameSession);
            }

            return gameSession;
        }
        else
        {
            LOG.error("Unable to load game session, repository not set - token: {}", token);

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
        if (gameRepository != null && (user == null || userRepository != null))
        {
            // Persist session
            try
            {
                gameRepository.updateGameSession(gameSession);
            }
            catch (Exception e)
            {
                LOG.error("Failed to persist game session", e);
                return;
            }

            // Persist user, if specified
            try
            {
                if (user != null)
                {
                    userRepository.updateUser(user);
                }
            }
            catch (Exception e)
            {
                LOG.error("Failed to persist user", e);
                return;
            }

            LOG.debug("Persisted game session - token: {}", gameSession.getToken());
        }
        else
        {
            LOG.error("Unable to persist game session, repository(s) not setup - token: {}", gameSession.getToken());
        }
    }

    /**
     * Routinely persist session data every 60 seconds in the event of an outage.
     */
    @Scheduled(fixedRate = 60000)
    public synchronized void persistSessions()
    {
        try
        {
            // Persist all tracked sessions
            int secondsSinceUpdated;
            for (GameSession gameSession : trackedGameSessions)
            {
                secondsSinceUpdated = Seconds.secondsBetween(
                        gameSession.getPlayerMetrics().getLastUpdated(), DateTime.now()).getSeconds();

                if (gameSession.getPlayerMetrics().isDirtyDatabaseFlag() || secondsSinceUpdated >= INTERVAL_UPDATE_SESSION)
                {
                    gameSession.getPlayerMetrics().setLastUpdatedNow();
                    gameRepository.updateGameSession(gameSession);
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Failed to perform periodic game session sync", e);
        }
    }

}
