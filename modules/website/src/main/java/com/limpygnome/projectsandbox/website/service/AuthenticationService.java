package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.website.model.account.User;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;

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
