package com.limpygnome.projectsandbox.website.controllers.pages.main;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import com.limpygnome.projectsandbox.website.model.form.home.GuestForm;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The home page for the website, used for registering/logging into an account and joining the game.
 */
@Controller
public class HomeController extends BaseController
{
    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home()
    {
        ModelAndView modelAndView = createMV("main/home", "welcome", "join");

        // Setup model
        modelAndView.addObject("user", authenticationService.retrieveCurrentUser());

        return modelAndView;
    }

    @ModelAttribute("guestForm")
    public GuestForm getGuestForm()
    {
        return new GuestForm();
    }

    @ModelAttribute("loginForm")
    public LoginForm getLoginForm()
    {
        return new LoginForm();
    }

    @ModelAttribute("registerForm")
    public RegisterForm getRegisterForm()
    {
        return new RegisterForm();
    }

}
