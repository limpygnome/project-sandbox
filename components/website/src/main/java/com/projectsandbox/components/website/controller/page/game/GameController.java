package com.projectsandbox.components.website.controller.page.game;

import com.projectsandbox.components.website.controller.BaseController;
import com.projectsandbox.components.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by limpygnome on 19/04/2015.
 */
@Controller
public class GameController extends BaseController
{
    public static final String MODEL_ATTRIB_GAME_SESSION_TOKEN = "token";

    private final static Logger LOG = LogManager.getLogger(GameController.class);

    private static final String GAME_SESSION_TOKEN_ATTRIB = "game_session";

    @Autowired
    private GameSessionService gameSessionService;

    @RequestMapping(value = {"/game"})
    public ModelAndView home(@ModelAttribute(MODEL_ATTRIB_GAME_SESSION_TOKEN) String gameSessionToken,
                             HttpSession httpSession)
    {
        // Pull token from session if not present
        if (gameSessionToken == null || gameSessionToken.length() == 0)
        {
            gameSessionToken = (String) httpSession.getAttribute(GAME_SESSION_TOKEN_ATTRIB);
        }

        // Check token is valid, else redirect back to home
        if (!gameSessionService.validateExists(gameSessionToken))
        {
            LOG.debug("Failed to validate and consume game session token - token: {}", gameSessionToken);
            return new ModelAndView("redirect:/home");
        }

        // Setup new page
        ModelAndView modelAndView = createMV("game/main", null);

        modelAndView.addObject("game_session_token", gameSessionToken);

        // Store token into session to allow refresh of page
        httpSession.setAttribute(GAME_SESSION_TOKEN_ATTRIB, gameSessionToken);

        return modelAndView;
    }

}
