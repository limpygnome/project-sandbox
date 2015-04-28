package com.limpygnome.projectsandbox.website.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 19/04/2015.
 */
@Controller
public class HomeController extends BaseController
{

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home()
    {
        ModelAndView mv = createMV("home", "welcome");

        return mv;
    }

}
