package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.website.jpa.models.User;

/**
 * Created by limpygnome on 17/07/15.
 */
public interface SessionService
{
    String generateSessionToken(String nickname);

    String generateSessionToken(User user);
}
