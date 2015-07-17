package com.limpygnome.projectsandbox.website.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Automatically provides CSRF protection on all forms.
 */
public class CsrfInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
        // Check if response has form
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {
        // Check if request is a postback; if so, check for csrf or redirect to error page
    }

    private String generateCsrfToken()
    {
        // UUID is sufficient; not likely someone can guess this...
        return UUID.randomUUID().toString();
    }
}
