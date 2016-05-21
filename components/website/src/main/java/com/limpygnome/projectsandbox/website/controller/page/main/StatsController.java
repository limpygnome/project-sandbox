package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.website.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 21/07/15.
 */
@Controller
public class StatsController extends BaseController
{

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ModelAndView stats()
    {
        return createMV("main/stats", "stats");
    }

}
