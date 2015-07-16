package com.limpygnome.projectsandbox.website.controllers.pages.game;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 19/04/2015.
 */
@Controller
public class GameController extends BaseController
{

    @RequestMapping(value = {"/", "/game"})
    public ModelAndView home()
    {
        ModelAndView modelAndView = createMV("game/main", null);

        return modelAndView;
    }

}
