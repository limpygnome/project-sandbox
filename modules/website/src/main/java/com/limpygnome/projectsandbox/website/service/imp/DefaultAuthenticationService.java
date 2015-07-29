package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateUserResult;
import com.limpygnome.projectsandbox.shared.model.Role;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.model.form.account.result.LoginResult;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultAuthenticationService implements AuthenticationService
{
    private static final String SESSION_ATTRIB_KEY = "user";

    private final static Logger LOG = LogManager.getLogger(DefaultAuthenticationService.class);

    @Value("${password.hash}")
    private String globalPasswordSalt;

    @Override
    public CreateUserResult register(HttpSession httpSession, RegisterForm registerForm)
    {
        // Check global salt is valid - important protection against Spring failing or w/e
        if (globalPasswordSalt == null || globalPasswordSalt.length() == 0 || globalPasswordSalt.startsWith("${"))
        {
            LOG.error("Global password salt incorrectly injected, cannot continue user registration");
            LOG.debug("Invalid global password salt - value: {}", globalPasswordSalt);
            return CreateUserResult.FAILED;
        }

        // Convert form into user model
        User user = new User(registerForm.getNickname(), registerForm.getEmail(), globalPasswordSalt,
                registerForm.getPassword()
        );

        // Store model in DB
        GameProvider gameProvider = new GameProvider();

        CreateUserResult createUserResult;
        try
        {
            gameProvider.begin();
            createUserResult = gameProvider.createUser(user);
            gameProvider.commit();
        }
        catch (Exception e)
        {
            gameProvider.rollback();

            LOG.error("Failed to persist user for registration", e);
            return CreateUserResult.FAILED;
        }
        finally
        {
            gameProvider.close();
        }

        if (createUserResult == CreateUserResult.SUCCESS)
        {
            // Automatically auth/login user
            httpSession.setAttribute(SESSION_ATTRIB_KEY, user);
        }

        return createUserResult;
    }

    @Override
    public LoginResult login(HttpSession httpSession, LoginForm loginForm)
    {
        // Check we have valid inputs
        if (httpSession == null || loginForm == null)
        {
            return LoginResult.FAILED;
        }

        GameProvider gameProvider = new GameProvider();

        try
        {
            // Fetch user
            User user = gameProvider.fetchUserByNickname(loginForm.getNickname());

            if (user == null)
            {
                return LoginResult.INCORRECT;
            }
            // Check password is correct
            else if (user.getPassword().isValid(this.globalPasswordSalt, loginForm.getPassword()))
            {
                // Check if user is banned
                if (user.getRoles().contains(Role.BANNED))
                {
                    return LoginResult.BANNED;
                }
                else
                {
                    // Push user into session
                    httpSession.setAttribute(SESSION_ATTRIB_KEY, user);
                    return LoginResult.SUCCESS;
                }
            }
            else
            {
                // TODO: login attempts / timeout etc
                return LoginResult.INCORRECT;
            }
        }
        catch (Exception e)
        {
            LOG.error("Failed to authenticate user - nickname: {}", loginForm.getNickname(), e);
            return LoginResult.FAILED;
        }
        finally
        {
            gameProvider.close();
        }
    }

    @Override
    public void logout(HttpSession httpSession)
    {
        if (httpSession != null)
        {
            httpSession.removeAttribute(SESSION_ATTRIB_KEY);
        }
    }

    @Override
    public User retrieveCurrentUser(HttpSession httpSession)
    {
        if (httpSession != null)
        {
            return (User) httpSession.getAttribute(SESSION_ATTRIB_KEY);
        }
        else
        {
            return null;
        }
    }

}
