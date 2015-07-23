package com.limpygnome.projectsandbox.website.controllers.pages.main;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import com.limpygnome.projectsandbox.website.controllers.pages.game.GameController;
import com.limpygnome.projectsandbox.website.model.User;
import com.limpygnome.projectsandbox.website.model.form.GuestForm;
import com.limpygnome.projectsandbox.website.model.form.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Used to authenticate users and join players into the game.
 */
@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController
{
    private final static Logger LOG = LogManager.getLogger(AuthController.class);

    @Autowired
    private GameSessionService gameSessionService;
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "guest", method = RequestMethod.POST)
    public ModelAndView joinGuest(@Valid GuestForm guestForm, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes)
    {
        // Check valid name provided
        if (bindingResult.hasErrors())
        {
            return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "guest", guestForm);
        }
        else
        {
            // Fetch guest session token
            String sessionToken = gameSessionService.generateSessionToken(guestForm.getNickname());

            return createGameRedirectModelAndView(sessionToken, redirectAttributes);
        }
    }

    @RequestMapping(value = "user")
    public ModelAndView joinUser(RedirectAttributes redirectAttributes)
    {
        // Fetch current user
        User user = authenticationService.retrieveCurrentUser();

        // Check the request is from an authenticated user
        if (user == null)
        {
            return createHomeRedirectModelAndView(null, null, null, null);
        }

        // Fetch account session token
        String sessionToken = gameSessionService.generateSessionToken(user);

        return createGameRedirectModelAndView(sessionToken, redirectAttributes);
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView accountRegister(@Valid RegisterForm registerForm, BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes)
    {
        // Register user; service will attach errors or success to bindingresult
        authenticationService.register(registerForm);

        return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "register", registerForm);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView accountLogin(@Valid LoginForm loginForm, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes)
    {
        // Attempt to login the user; service attaches error/success to bindingresult
        authenticationService.login(loginForm);

        return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "login", loginForm);
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView accountLogout()
    {
        authenticationService.logout();

        return createHomeRedirectModelAndView(null, null, null, null);
    }

    private ModelAndView createGameRedirectModelAndView(String sessionToken, RedirectAttributes redirectAttributes)
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/game");

        // Store in session, less likely to be able to create fraudulent guest tokens
        redirectAttributes.addFlashAttribute(GameController.MODEL_ATTRIB_GAME_SESSION_TOKEN, sessionToken);

        return modelAndView;
    }

    private ModelAndView createHomeRedirectModelAndView(BindingResult bindingResult,
                                                        RedirectAttributes redirectAttributes,
                                                        String formName,
                                                        Object formData) {

        ModelAndView modelAndView = new ModelAndView("redirect:/home");

        // Add binding result to redirect
        if (bindingResult != null && redirectAttributes != null && formName != null && formData != null)
        {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + formName, bindingResult);
            redirectAttributes.addFlashAttribute(formName, formData);

            LOG.debug("Form validation errors passed back to home");
        }

        return modelAndView;
    }

}
