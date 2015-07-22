package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.website.model.User;
import com.limpygnome.projectsandbox.website.model.form.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.RegisterForm;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface AuthenticationService
{
    User register(RegisterForm registerForm);

    boolean login(LoginForm loginForm);

    boolean logout();

    User retrieveCurrentUser();
}
