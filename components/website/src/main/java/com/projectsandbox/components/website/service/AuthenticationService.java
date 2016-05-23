package com.projectsandbox.components.website.service;

import com.projectsandbox.components.shared.jpa.repository.result.CreateUserResult;
import com.projectsandbox.components.shared.model.User;
import com.projectsandbox.components.website.model.result.LoginResult;
import com.projectsandbox.components.website.model.form.home.LoginForm;
import com.projectsandbox.components.website.model.form.home.RegisterForm;

import javax.servlet.http.HttpSession;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface AuthenticationService
{
    CreateUserResult register(HttpSession httpSession, RegisterForm registerForm);

    LoginResult login(HttpSession httpSession, LoginForm loginForm);

    void logout(HttpSession httpSession);

    User retrieveCurrentUser(HttpSession httpSession);

    String getGlobalPasswordSalt();
}
