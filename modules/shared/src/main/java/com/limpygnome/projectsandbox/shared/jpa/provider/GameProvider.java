package com.limpygnome.projectsandbox.shared.jpa.provider;

import com.limpygnome.projectsandbox.shared.jpa.AbstractProvider;
import com.limpygnome.projectsandbox.shared.jpa.ConnectionType;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateGameSessionResult;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

import static com.limpygnome.projectsandbox.shared.constant.SessionConstants.TIMEOUT_INITIAL_CONNECTION_SECONDS;
import static com.limpygnome.projectsandbox.shared.constant.SessionConstants.TIMEOUT_LAST_UPDATED_SECONDS;

/**
 * Created by limpygnome on 25/07/15.
 */
public class GameProvider extends AbstractProvider
{
    private final static Logger LOG = LogManager.getLogger(GameProvider.class);

    public GameProvider()
    {
        super(ConnectionType.PROJECTSANDBOX);
    }

    public GameSession fetchGameSessionByUser(User user)
    {
        try
        {
            TypedQuery<GameSession> typedQuery = em.createQuery("SELECT gs FROM GameSession gs WHERE user = :user", GameSession.class);
            typedQuery.setParameter("user", user);

            List<GameSession> gameSessions = typedQuery.getResultList();

            return gameSessions.isEmpty() ? null : gameSessions.get(0);
        }
        catch (Exception e)
        {
            LOG.error("Failed to query for existing game session", e);
            return null;
        }
    }

    public GameSession fetchGameSessionByToken(UUID token)
    {
        try
        {
            String rawToken = token.toString();

            TypedQuery<GameSession> typedQuery = em.createQuery("SELECT gs FROM GameSession gs WHERE token = :token", GameSession.class);
            typedQuery.setParameter("token", rawToken);

            List<GameSession> result = typedQuery.getResultList();

            if (result.isEmpty())
            {
                LOG.debug("Game session not found - token: {}", rawToken);
                return null;
            }
            else
            {
                if (result.size() > 1)
                {
                    LOG.warn("Multiple game sessions found - token: {}", rawToken);
                }

                return result.get(0);
            }
        }
        catch (Exception e)
        {
            LOG.debug("Failed to query for existing game session - uuid: {}", token, e);
            return null;
        }
    }

    public CreateGameSessionResult createGameSession(GameSession gameSession)
    {

        try
        {
            // Check game session does not exist
            TypedQuery<GameSession> typedQuery = em.createQuery("SELECT gs FROM GameSession gs WHERE token = :token", GameSession.class);
            typedQuery.setParameter("token", gameSession.getToken());

            typedQuery.getSingleResult();

            LOG.error("Cannot create game session, token already exists - token: {}", gameSession.getToken());

            return CreateGameSessionResult.EXISTS;
        }
        catch (NoResultException e)
        {
            // Persist session
            try
            {
                em.persist(gameSession);

                LOG.info("Created new game session - uuid: {}", gameSession.getToken());

                return CreateGameSessionResult.SUCCESS;
            }
            catch (Exception e2)
            {
                LOG.error("Failed to create new game session", e2);

                return CreateGameSessionResult.FAILED;
            }
        }
        catch (Exception e)
        {
            return CreateGameSessionResult.FAILED;
        }
    }

    public boolean updateGameSession(GameSession gameSession)
    {
        try
        {
            em.merge(gameSession);

            LOG.debug("Updated game session - token: {}", gameSession.getToken());

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Failed to update game session - token: {}", gameSession.getToken(), e);

            return false;
        }
    }

    public boolean removeGameSession(GameSession gameSession)
    {
        try
        {
            // Make sure model is within context
            em.merge(gameSession);

            // Finally remove
            em.remove(gameSession);

            LOG.info("Removed game session - uuid: {}", gameSession.getToken());

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Failed to remove game session - uuid: {}", gameSession.getToken(), e);

            return false;
        }
    }

    public void setAllSessionsToDisconnected()
    {
        try
        {
            Query query = em.createQuery("UPDATE GameSession SET connected = false WHERE connected = true");
            int rowsAffected = query.executeUpdate();

            if (rowsAffected > 0)
            {
                LOG.info("Previously connected sessions set to disconnected - count: {}", rowsAffected);
            }
        }
        catch (Exception e)
        {
            LOG.error("Failed to set all sessions to disconnected", e);
        }
    }

    public boolean removeInactiveGuestGameSessions()
    {
        try
        {
            Query query = em.createQuery(
                    "DELETE FROM GameSession WHERE " +
                            "user = null AND connected = false AND " +
                            "((created != last_updated AND created <= :created) OR last_updated <= :updated)"
            );

            query.setParameter("created", DateTime.now().minusSeconds(TIMEOUT_INITIAL_CONNECTION_SECONDS));
            query.setParameter("updated", DateTime.now().minusSeconds(TIMEOUT_LAST_UPDATED_SECONDS).toDate());

            int affectedRows = query.executeUpdate();

            if (affectedRows > 0)
            {
                LOG.info("Removed inactive game sessions - count: {}", affectedRows);
            }
            else
            {
                LOG.debug("No inactive game sessions");
            }

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Failed to remove all game sessions", e);
            return false;
        }
    }

//    public CreateUserResult createUser(User user)
//    {
//    }
//
//    public void removeUser(User user)
//    {
//    }

}
