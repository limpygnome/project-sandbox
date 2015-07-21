package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.jpa.models.User;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.springframework.stereotype.Service;

/**
 * Created by limpygnome on 17/07/15.
 */
@Service
public class DefaultGameSessionService implements GameSessionService
{
    @Override
    public String generateSessionToken(String nickname)
    {
        return null;
    }

    @Override
    public String generateSessionToken(User user)
    {
        return null;
    }
}
