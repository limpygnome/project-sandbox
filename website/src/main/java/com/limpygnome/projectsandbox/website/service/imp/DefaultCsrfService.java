package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.service.CsrfService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by limpygnome on 20/07/15.
 */
@Service
public class DefaultCsrfService implements CsrfService
{
    public static final String PARAMETER_ATTRIB_CSRF = "csrf";
    private static final String SESSION_ATTRIB_TOKEN = "csrf-token";

    @Override
    public String generateToken(HttpServletRequest httpServletRequest)
    {
        String token = UUID.randomUUID().toString();

        // Store token in session
        HttpSession httpSession = httpServletRequest.getSession(false);
        httpSession.setAttribute(SESSION_ATTRIB_TOKEN, token);

        return token;
    }

    @Override
    public boolean isValidRequest(HttpServletRequest httpServletRequest)
    {
        // Retrieve request token
        String requestToken = httpServletRequest.getParameter(PARAMETER_ATTRIB_CSRF);

        if (requestToken == null || requestToken.length() == 0)
        {
            return false;
        }

        // Retrieve token and validate
        HttpSession httpSession = httpServletRequest.getSession(false);

        String sessionToken = (String) httpSession.getAttribute(SESSION_ATTRIB_TOKEN);

        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
