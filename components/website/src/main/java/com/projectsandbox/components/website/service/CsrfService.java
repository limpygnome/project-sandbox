package com.projectsandbox.components.website.service;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * A service for providing and validating CSRF tokens.
 */
public interface CsrfService
{
    void generateToken(HttpServletRequest httpServletRequest, ModelAndView modelAndView);

    boolean isValidRequest(HttpServletRequest httpServletRequest);
}
