package com.limpygnome.projectsandbox.shared.jpa.repository;

import com.limpygnome.projectsandbox.shared.jpa.repository.result.CreateGameSessionResult;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static com.limpygnome.projectsandbox.shared.constant.SessionConstants.TIMEOUT_INITIAL_CONNECTION_SECONDS;
import static com.limpygnome.projectsandbox.shared.constant.SessionConstants.TIMEOUT_LAST_UPDATED_SECONDS;
import static com.limpygnome.projectsandbox.shared.jpa.JpaConstants.PERSISTENCE_UNIT_NAME;

/**
 * Created by limpygnome on 25/07/15.
 *
 * TODO: add more checking / safety
 */
@Repository
public class GameRepository
{
    private final static Logger LOG = LogManager.getLogger(GameRepository.class);

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    @Transactional
    public String fetchExistingGameSessionToken(User user)
    {
        if (user == null)
        {
            LOG.error("Attempted to fetch game session by null user");
            return null;
        }

        try
        {
            TypedQuery<String> typedQuery = em.createQuery("SELECT gs.token FROM GameSession gs WHERE user = :user", String.class);
            typedQuery.setParameter("user", user);

            List<String> tokens = typedQuery.getResultList();
            return tokens.isEmpty() ? null : tokens.get(0);
        }
        catch (Exception e)
        {
            LOG.error("Failed to query for existing game session", e);
            return null;
        }
    }

    @Transactional
    public GameSession fetchGameSessionByUser(User user)
    {
        if (user == null)
        {
            LOG.error("Attempted to fetch game session by null user");
            return null;
        }

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

    @Transactional
    public GameSession fetchGameSessionByToken(UUID token)
    {
        if (token == null)
        {
            LOG.error("Attempted to fetch game session by null token");
            return null;
        }

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

    @Transactional
    public boolean isTokenValid(String gameSessionToken)
    {
        TypedQuery<Long> typedQuery = em.createQuery("SELECT count(gs) FROM GameSession gs WHERE token = :token", Long.class);
        typedQuery.setParameter("token", gameSessionToken);

        List<Long> result = typedQuery.getResultList();
        boolean exists = result.isEmpty() ? false : result.get(0) == 1;
        return exists;
    }

    @Transactional
    public CreateGameSessionResult createGameSession(GameSession gameSession)
    {
        if (gameSession == null)
        {
            LOG.error("Attempted to create null game session");
            return CreateGameSessionResult.FAILED;
        }

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
                // Persist and immediately ensure the DB receives the changes
                em.persist(gameSession);
                em.flush();

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
            LOG.error("Failed to setup game session", e);

            return CreateGameSessionResult.FAILED;
        }
    }

    @Transactional
    public boolean updateGameSession(GameSession gameSession)
    {
        if (gameSession == null)
        {
            LOG.error("Attempted to update null game session");
            return false;
        }

        try
        {
            em.merge(gameSession);
            //em.persist(gameSession);
            em.flush();

            LOG.debug("Updated game session - token: {}", gameSession.getToken());

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Failed to update game session - token: {}", gameSession.getToken(), e);

            return false;
        }
    }

    @Transactional
    public boolean removeGameSession(String token)
    {
        if (token == null)
        {
            LOG.error("Attempted to remove game session with null token");
            return false;
        }

        try
        {
            // Build query
            Query query = em.createQuery("DELETE FROM GameSession gs WHERE gs.token = :token");
            query.setParameter("token", token);

            // Execute query
            int rowsDeleted = query.executeUpdate();
            boolean success = rowsDeleted > 0;

            if (success)
            {
                LOG.info("removed game session - uuid: {}", token);
            }
            else
            {
                LOG.warn("no game session found for deletion - uuid: {}", token);
            }

            return success;
        }
        catch (Exception e)
        {
            LOG.error("Failed to remove game session - uuid: {}", token, e);
            return false;
        }
    }

    @Transactional
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

    @Transactional
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

}
