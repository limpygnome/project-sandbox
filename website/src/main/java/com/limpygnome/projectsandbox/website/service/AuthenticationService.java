package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.website.jpa.models.User;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface AuthenticationService
{
    User register(String username, String password, String email, String nickname);

    boolean login(String username, String password);

    boolean logout();

    User retrieveCurrentUser();
}
