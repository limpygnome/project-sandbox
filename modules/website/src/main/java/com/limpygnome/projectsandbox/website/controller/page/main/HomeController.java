package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.home.GuestForm;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * The home page for the website, used for registering/logging into an account and joining the game.
 */
@Controller
public class HomeController extends BaseController
{
    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home(HttpSession httpSession)
    {
        ModelAndView modelAndView = createMV("main/home", "welcome", "join");

        // Setup model
        modelAndView.addObject("user", authenticationService.retrieveCurrentUser(httpSession));

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
