package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.shared.jpa.provider.result.CreateUserResult;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;

import javax.servlet.http.HttpSession;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface AuthenticationService
{
    CreateUserResult register(HttpSession httpSession, RegisterForm registerForm);

    boolean login(HttpSession httpSession, LoginForm loginForm);

    void logout(HttpSession httpSession);

    User retrieveCurrentUser(HttpSession httpSession);
}
