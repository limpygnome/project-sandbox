package com.limpygnome.projectsandbox.website.controller.page.game;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.service.GameSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 19/04/2015.
 */
@Controller
public class GameController extends BaseController
{
    public static final String MODEL_ATTRIB_GAME_SESSION_TOKEN = "token";

    private final static Logger LOG = LogManager.getLogger(GameController.class);

    @Autowired
    private GameSessionService gameSessionService;

    @RequestMapping(value = {"/game"})
    public ModelAndView home(@ModelAttribute(MODEL_ATTRIB_GAME_SESSION_TOKEN) String gameSessionToken)
    {
        GameProvider gameProvider = new GameProvider();

        // Check token is valid, else redirect back to home
        if (!gameSessionService.validateAndConsume(gameProvider, gameSessionToken))
        {
            LOG.debug("Failed to validate and consume game session token");
            return new ModelAndView("redirect:/home");
        }

        // Setup new page
        ModelAndView modelAndView = createMV("game/main", null);

        modelAndView.addObject("game_session_token", gameSessionToken);

        gameProvider.close();

        return modelAndView;
    }

}
