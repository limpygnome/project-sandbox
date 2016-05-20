package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.shared.jpa.repository.GameRepository;
import com.limpygnome.projectsandbox.shared.jpa.repository.result.CreateUserResult;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.controller.page.game.GameController;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.model.result.LoginResult;
import com.limpygnome.projectsandbox.website.model.form.home.GuestForm;
import com.limpygnome.projectsandbox.website.model.form.home.LoginForm;
import com.limpygnome.projectsandbox.website.model.form.home.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
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
    public ModelAndView joinGuest(@ModelAttribute("guestForm") @Valid GuestForm guestForm, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes)
    {
        ModelAndView modelAndView;

        // Check valid name provided
        if (bindingResult.hasErrors())
        {
            modelAndView = createHomeRedirectModelAndView(bindingResult, redirectAttributes, "guestForm", guestForm);
        }
        else
        {
            // Fetch guest session token
            String nickname = guestForm.getNickname();
            String sessionToken = gameSessionService.fetchOrGenerateSessionToken(nickname);

            LOG.debug("Session token retrieved - token: {}", sessionToken);

            modelAndView = joinSession(sessionToken, bindingResult, redirectAttributes, guestForm);
        }

        return modelAndView;
    }

    @RequestMapping(value = "user")
    public ModelAndView joinUser(RedirectAttributes redirectAttributes,
                                 HttpSession httpSession)
    {
        ModelAndView modelAndView;

        // Fetch current user
        User user = authenticationService.retrieveCurrentUser(httpSession);

        // Check the request is from an authenticated user
        if (user == null)
        {
            return createHomeRedirectModelAndView(null, null, null, null);
        }

        // Fetch account session token
        String sessionToken = gameSessionService.fetchOrGenerateSessionToken(user);

        // Determine model-view for joining session
        modelAndView = joinSession(sessionToken, null, redirectAttributes, null);

        return modelAndView;
    }

    private ModelAndView joinSession(String sessionToken, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes, Object formData)
    {
        // Check we got a valid session token, in case service unavailable etc...
        if (sessionToken != null)
        {
            return createGameRedirectModelAndView(sessionToken, redirectAttributes);
        }
        else
        {
            // Add error explaining why we've redirected back to home controller
            if (bindingResult != null)
            {
                bindingResult.reject("session.token.failure");
            }

            return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "guestForm", formData);
        }
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView accountRegister(@ModelAttribute("registerForm") @Valid RegisterForm registerForm,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes, HttpSession httpSession)
    {
        // Register user
        if (registerForm != null && !bindingResult.hasErrors())
        {
            CreateUserResult createUserResult = authenticationService.register(httpSession, registerForm);

            switch (createUserResult)
            {
                case FAILED:
                    bindingResult.reject("register.failure");
                    break;
                case EMAIL_EXISTS:
                    bindingResult.reject("register.email-exists");
                    break;
                case NICKNAME_EXISTS:
                    bindingResult.reject("register.nickname-exists");
                    break;
            }
        }

        return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "registerForm", registerForm);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView accountLogin(@ModelAttribute("loginForm") @Valid LoginForm loginForm, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes, HttpSession httpSession)
    {
        // Attempt to login the user
        if (!bindingResult.hasErrors())
        {
            LoginResult loginResult = authenticationService.login(httpSession, loginForm);

            switch (loginResult)
            {
                case FAILED:
                    bindingResult.reject("login.failure");
                    break;
                case INCORRECT:
                    bindingResult.reject("login.incorrect");
                    break;
                case BANNED:
                    bindingResult.reject("login.banned");
                    break;
            }
        }

        return createHomeRedirectModelAndView(bindingResult, redirectAttributes, "loginForm", loginForm);
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView accountLogout(HttpSession httpSession)
    {
        authenticationService.logout(httpSession);

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
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + formName, bindingResult);
            redirectAttributes.addFlashAttribute(formName, formData);

            LOG.debug("Form validation errors passed back to home");
        }

        return modelAndView;
    }

}
