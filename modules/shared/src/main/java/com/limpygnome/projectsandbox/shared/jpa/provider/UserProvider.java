package com.limpygnome.projectsandbox.shared.jpa.provider;

import com.limpygnome.projectsandbox.shared.jpa.AbstractProvider;
import com.limpygnome.projectsandbox.shared.jpa.ConnectionType;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateUserResult;
import com.limpygnome.projectsandbox.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by limpygnome on 07/08/15.
 *
 * TODO: add more checking / safety
 */
public class UserProvider extends AbstractProvider
{
    private final static Logger LOG = LogManager.getLogger(UserProvider.class);

    public UserProvider()
    {
        super(ConnectionType.PROJECTSANDBOX);
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
            em.flush();

            LOG.info("Created new user - user id: {}, nickname: {}", user.getUserId(), user.getNickname());

            return CreateUserResult.SUCCESS;
        }
        catch (Exception e)
        {
            LOG.error("Failed to persist new user", e);
            return CreateUserResult.FAILED;
        }
    }

    public User fetchUserByUserId(String userId)
    {
        if (userId == null)
        {
            LOG.error("Attempted to fetch user by null userId");
            return null;
        }

        try
        {
            TypedQuery<User> typedQuery = em.createQuery("SELECT u FROM User u WHERE u.userId = :userid", User.class);
            typedQuery.setParameter("userid", userId);

            List<User> users = typedQuery.getResultList();

            return users.isEmpty() ? null : users.get(0);
        }
        catch (Exception e)
        {
            LOG.error("Failed to retrieve user by userId - user id: {}", userId, e);
            return null;
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
            // Fetch proxy/reference instance for removal to be in an attached state
            User attachedUser = em.getReference(User.class, user.getUserId());
            em.remove(attachedUser);
            em.flush();

            LOG.debug("Removed user - user id: {}", user.getUserId());
            return true;
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
