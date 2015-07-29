package com.limpygnome.projectsandbox.website.interceptor;

import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by limpygnome on 28/07/15.
 */
public class UserInterceptor implements HandlerInterceptor
{
    private static final String MODEL_ATTRIB_USER = "user";

    private AuthenticationService authenticationService;

    public UserInterceptor(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception
    {
        // Check user is authorized to visit resource
        User user = authenticationService.retrieveCurrentUser(httpServletRequest.getSession(false));

        // Get the request path for restricting paths
        String requestPath = httpServletRequest.getServletPath();

        if (requestPath.startsWith("/"))
        {
            if (requestPath.length() == 1)
            {
                requestPath = "";
            }
            else
            {
                requestPath = requestPath.substring(1);
            }
        }

        // Restrict certain paths to users only
        if (user == null)
        {
            if  (
                    requestPath.startsWith("account") ||
                    requestPath.startsWith("auth/user") ||
                    requestPath.startsWith("auth/logout")
                )
            {
                httpServletResponse.sendRedirect("/home");
                return false;
            }
        }
        else
        {
            if  (
                    requestPath.startsWith("auth/guest") ||
                    requestPath.startsWith("control")
                )
            {
                httpServletResponse.sendRedirect("/home");
                return false;
            }
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception
    {
        if (modelAndView != null)
        {
            // Fetch current user, place into model-view
            User user = authenticationService.retrieveCurrentUser(httpServletRequest.getSession(false));

            if (user != null)
            {
                modelAndView.addObject(MODEL_ATTRIB_USER, user);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception
    {
        // Do nothing...
    }

}
