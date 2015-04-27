package com.projectsandbox.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 27/04/15.
 */
@Controller
public class BaseController
{
    public ModelAndView create(String page)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName(page);

        return mv;
    }
}
