package com.limpygnome.projectsandbox.shared.jpa.provider;

import com.limpygnome.projectsandbox.shared.jpa.AbstractProvider;
import com.limpygnome.projectsandbox.shared.jpa.ConnectionType;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateGameSessionResult;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateUserResult;
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
 *
 * TODO: add more checking / safety
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
        if (gameSession == null)
        {
            LOG.error("Attempted to update null game session");
            return false;
        }

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
        if (gameSession == null)
        {
            LOG.error("Attempted to remove null game session");
            return false;
        }

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

    public CreateUserResult createUser(User user)
    {
        if (user == null)
        {
            LOG.error("Attempted to create null user");
            return CreateUserResult.FAILED;
        }

        Query query;
        long count;

        try
        {
            // Check nickname not already used
            query = em.createQuery("SELECT COUNT(u.userId) FROM User u WHERE u.nickname = :nickname");
            query.setParameter("nickname", user.getNickname());
            count = (long) query.getSingleResult();

            if (count != 0)
            {
                return CreateUserResult.NICKNAME_EXISTS;
            }

            // Check email not already used
            query = em.createQuery("SELECT COUNT(u.userId) FROM User u WHERE u.email = :email");
            query.setParameter("email", user.getEmail());
            count = (long) query.getSingleResult();

            if (count != 0)
            {
                return CreateUserResult.EMAIL_EXISTS;
            }

            // Persist the user
            em.persist(user);

            return CreateUserResult.SUCCESS;
        }
        catch (Exception e)
        {
            LOG.error("Failed to persist new user", e);
            return CreateUserResult.FAILED;
        }
    }

    public User fetchUserByNickname(String nickname)
    {
        if (nickname == null)
        {
            LOG.error("Attempted to fetch user by null nickname");
            return null;
        }

        try
        {
            TypedQuery<User> typedQuery = em.createQuery("SELECT u FROM User u WHERE u.nickname = :nickname", User.class);
            typedQuery.setParameter("nickname", nickname);

            List<User> users = typedQuery.getResultList();

            return users.isEmpty() ? null : users.get(0);
        }
        catch (Exception e)
        {
            LOG.error("Failed to retrieve user by nickname - nickname: {}", nickname, e);
            return null;
        }
    }

    public boolean updateUser(User user)
    {
        if (user == null)
        {
            LOG.error("Attempted to update null user");
            return false;
        }

        try
        {
            em.merge(user);

            LOG.debug("Updated user - user id: {}", user.getUserId());
            return true;
        }
        catch (Exception e)
        {
            LOG.error("Failed to update user - user id: {}", user.getUserId(), e);
            return false;
        }
    }

    public boolean removeUser(User user)
    {
        if (user == null)
        {
            LOG.error("Attempted to remove null user");
            return false;
        }

        try
        {
            Query query = em.createQuery("DELETE FROM User WHERE userId = :userid");
            query.setParameter("userid", user.getUserId());

            LOG.debug("Removed user - user id: {}", user.getUserId());
            return query.executeUpdate() > 0;
        }
        catch (Exception e)
        {
            LOG.error("Failed to remove user - user id: {}", user.getUserId(), e);
            return false;
        }
    }

    public long getUsersOnline()
    {
        try
        {
            Query query = em.createQuery("SELECT COUNT(g.token) FROM GameSession g WHERE g.connected = true");
            return (long) query.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.error("Failed to retrieve total users online", e);
            return 0;
        }
    }

}