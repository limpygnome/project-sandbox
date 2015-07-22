package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.model.User;
import com.limpygnome.projectsandbox.website.model.form.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultAuthenticationService implements AuthenticationService
{
    @Override
    public User register(RegisterForm registerForm)
    {
        return null;
    }

    @Override
    public boolean login(LoginForm loginForm)
    {
        return false;
    }

    @Override
    public boolean logout()
    {
        return true;
    }

    @Override
    public User retrieveCurrentUser()
    {
        return null;
    }
}
