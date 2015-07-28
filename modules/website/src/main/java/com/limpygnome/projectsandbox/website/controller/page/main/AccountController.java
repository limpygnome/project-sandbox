package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used by the user for changing their account.
 */
@Controller
public class AccountController extends BaseController
{

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ModelAndView accountView()
    {
        return createMV("account");
    }

    @RequestMapping(value = "/account/update")
    public ModelAndView accountProcessUpdate()
    {
        // Update properties for user


        return redirectToAccountView();
    }

    @RequestMapping(value = "/account/delete")
    public ModelAndView accountProcessDelete()
    {
        // Set account for deletion

        return redirectToAccountView();
    }

    public ModelAndView redirectToAccountView()
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");

        return modelAndView;
    }

}
