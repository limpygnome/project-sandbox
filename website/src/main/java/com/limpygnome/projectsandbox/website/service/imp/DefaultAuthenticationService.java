package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.jpa.models.User;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultAuthenticationService implements AuthenticationService
{
    @Override
    public User register(String username, String password, String email, String nickname)
    {
        return null;
    }

    @Override
    public boolean login(String username, String password)
    {
        return false;
    }

    @Override
    public boolean logout()
    {
        return false;
    }

    @Override
    public User retrieveCurrentUser()
    {
        return null;
    }
}
