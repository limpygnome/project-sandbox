package com.limpygnome.projectsandbox.website.controllers.pages.main;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import com.limpygnome.projectsandbox.website.controllers.pages.game.GameController;
import com.limpygnome.projectsandbox.website.jpa.models.User;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Used to authenticate users and join players into the game.
 */
@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController
{
    @Autowired
    private GameSessionService gameSessionService;
    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = "guest", method = RequestMethod.POST)
    public ModelAndView joinGuest(@RequestParam("nickname") String nickname, RedirectAttributes redirectAttributes)
    {
        // Fetch guest session token
        String sessionToken = gameSessionService.generateSessionToken(nickname);

        return createGameRedirectModelAndView(sessionToken, redirectAttributes);
    }

    @RequestMapping(value = "user")
    public ModelAndView joinUser(RedirectAttributes redirectAttributes)
    {
        // Fetch current user
        User user = authenticationService.retrieveCurrentUser();

        if (user == null)
        {
            // User must not be logged in...
            return new ModelAndView("redirect:/home");
        }

        // Fetch account session token
        String sessionToken = gameSessionService.generateSessionToken(user);

        return createGameRedirectModelAndView(sessionToken, redirectAttributes);
    }

    private ModelAndView createGameRedirectModelAndView(String sessionToken, RedirectAttributes redirectAttributes)
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/game");

        // Store in session, less likely to be able to create fraudulent guest tokens
        redirectAttributes.addFlashAttribute(GameController.MODEL_ATTRIB_GAME_SESSION_TOKEN, sessionToken);

        return modelAndView;
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView accountRegister(@RequestParam("user")       String username,
                                        @RequestParam("pass")       String password,
                                        @RequestParam("email")      String email,
                                        @RequestParam("nickname")   String nickname)
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/home");

        // Register user; service will attach errors or success to bindingresult
        authenticationService.register(username, password, email, nickname);

        return modelAndView;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView accountLogin(@RequestParam("user") String username, @RequestParam("pass") String password)
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/home");

        // Attempt to login the user; service attaches error/success to bindingresult
        authenticationService.login(username, password);

        return modelAndView;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView accountLogout()
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/home");

        authenticationService.logout();

        return modelAndView;
    }

}
