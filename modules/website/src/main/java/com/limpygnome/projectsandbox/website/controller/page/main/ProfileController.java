package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

/**
 * Created by limpygnome on 07/08/15.
 */
@Controller
@RequestMapping(value = "/profile")
public class ProfileController extends BaseController
{

    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^([a-fA-F0-9]{8})\\-(([a-fA-F0-9]{4})\\-){3}([a-fA-F0-9]{12})$");

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "")
    public void viewCurrentUser(HttpSession httpSession)
    {
        // Fetch and render profile for current user
        User currentUser = authenticationService.retrieveCurrentUser(httpSession);

        viewProfile(currentUser);
    }

    @RequestMapping(value = "{1}")
    public void viewUser(@RequestParam(required = true) String userId)
    {
        User profileUser;

        // Fetch user and render their profile
        if (UUID_REGEX_PATTERN.matcher(userId).matches())
        {
            // Load by UUID
        }
        else
        {
            // Load by nickname
        }

        viewProfile(profileUser);
    }

    public ModelAndView viewProfile(User user)
    {
        // Setup mv
        ModelAndView modelAndView = createMV("main/profile", "profile - " + user.getNickname());

        // Attach objects
        modelAndView.addObject("profile_user", user);

        return modelAndView;
    }

}
