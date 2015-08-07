package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.jpa.provider.UserProvider;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.home.GuestForm;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * The home page for the website, used for registering/logging into an account and joining the game.
 */
@Controller
public class HomeController extends BaseController
{

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home(HttpSession httpSession, @RequestParam(value = "csrf", required = false) String csrf)
    {
        ModelAndView modelAndView = createMV("main/home", "welcome", "join");

        // Add players online
        UserProvider userProvider = new UserProvider();
        modelAndView.addObject("playersOnline", userProvider.getUsersOnline());
        userProvider.close();

        // Add CSRF flag if set for request
        if (csrf != null)
        {
            modelAndView.addObject("csrf", true);
        }

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
