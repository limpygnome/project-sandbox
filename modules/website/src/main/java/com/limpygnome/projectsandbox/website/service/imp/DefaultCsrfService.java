package com.limpygnome.projectsandbox.website.service.imp;

import com.limpygnome.projectsandbox.website.service.CsrfService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * Created by limpygnome on 20/07/15.
 */
@Service
public class DefaultCsrfService implements CsrfService
{
    private final static Logger LOG = LogManager.getLogger(DefaultCsrfService.class);

    public static final String PARAMETER_ATTRIB_CSRF = "csrf";
    private static final String SESSION_ATTRIB_TOKEN = "csrf-token";

    @Override
    public void generateToken(HttpServletRequest httpServletRequest, ModelAndView modelAndView)
    {
        if (httpServletRequest != null)
        {
            String token = UUID.randomUUID().toString();

            // Store token in session
            HttpSession httpSession = httpServletRequest.getSession(true);

            if (httpSession != null)
            {
                httpSession.setAttribute(SESSION_ATTRIB_TOKEN, token);

                LOG.debug("CSRF session token changed - token: {}, request path: {}", token, httpServletRequest.getRequestURI());

                // Append to model
                if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:"))
                {
                    modelAndView.addObject(PARAMETER_ATTRIB_CSRF, token);
                }
            }
        }
    }

    @Override
    public boolean isValidRequest(HttpServletRequest httpServletRequest)
    {
        if (httpServletRequest != null)
        {
            return true;
        }
        if (httpServletRequest != null)
        {
            // Retrieve request token
            String requestToken = httpServletRequest.getParameter(PARAMETER_ATTRIB_CSRF);

            if (requestToken == null)
            {
                // Attempt to see if request is multipart
                String contentType = httpServletRequest.getContentType();

                if (contentType != null && contentType.startsWith("multipart"))
                {
                    return true;
//                    try
//                    {
//                        FileItemFactory fileItemFactory = new DiskFileItemFactory();
//                        ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
//                        List<FileItem> uploadedItems = servletFileUpload.parseRequest(httpServletRequest);
//
//                        for (FileItem uploadedItem : uploadedItems)
//                        {
//                            if (uploadedItem.isFormField() && PARAMETER_ATTRIB_CSRF.equals(uploadedItem.getFieldName()))
//                            {
//                                requestToken = uploadedItem.getString();
//                                LOG.debug("Found CSRF token in multipart - {}", requestToken);
//                                break;
//                            }
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        // Keep to debug to avoid log spam attack
//                        LOG.debug("Failed to parse multipart for CSRF token", e);
//                    }
                }
            }

            if (requestToken == null || requestToken.length() == 0)
            {
                LOG.debug("CSRF token missing from request");
                return false;
            }

            // Retrieve token and validate
            HttpSession httpSession = httpServletRequest.getSession(false);

            if (httpSession != null)
            {
                Object rawSessionToken = httpSession.getAttribute(SESSION_ATTRIB_TOKEN);

                if (rawSessionToken == null)
                {
                    LOG.debug("No CSRF token in session");
                    return false;
                }

                String sessionToken = (String) rawSessionToken;

                boolean sessionMatchesRequestToken = sessionToken.length() != 0 && sessionToken.equals(requestToken);

                if (!sessionMatchesRequestToken)
                {
                    LOG.debug(
                            "CSRF session token does not match request token - session token: {}, request token: {}",
                            sessionToken,
                            requestToken
                    );
                }

                return sessionMatchesRequestToken;
            }
        }

        return false;
    }

}
