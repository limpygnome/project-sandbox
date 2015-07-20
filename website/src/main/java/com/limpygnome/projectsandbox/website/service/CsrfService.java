package com.limpygnome.projectsandbox.website.service;

import javax.servlet.http.HttpServletRequest;

/**
 * A service for providing and validating CSRF tokens.
 */
public interface CsrfService
{
    String generateToken(HttpServletRequest httpServletRequest);

    boolean isValidRequest(HttpServletRequest httpServletRequest);
}
