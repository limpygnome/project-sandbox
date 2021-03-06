package com.projectsandbox.components.website.interceptor;

import com.projectsandbox.components.website.service.CsrfService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Automatically provides CSRF protection on all forms.
 */
public class CsrfInterceptor implements HandlerInterceptor
{
    private final static Logger LOG = LogManager.getLogger(CsrfInterceptor.class);

    private CsrfService csrfService;

    public CsrfInterceptor(CsrfService csrfService)
    {
        this.csrfService = csrfService;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        // Check if CSRF present
        String method = httpServletRequest.getMethod();

        if (method != null)
        {
            method = method.toLowerCase();

            if ((method.equals("post") || method.equals("put") || method.equals("delete")) && !csrfService.isValidRequest(httpServletRequest))
            {
                LOG.warn("CSRF attempt - ip: {}", httpServletRequest.getRemoteAddr());
                httpServletResponse.sendRedirect("/home?csrf=1");
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
        csrfService.generateToken(httpServletRequest, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {
        // Do nothing...
    }
}
