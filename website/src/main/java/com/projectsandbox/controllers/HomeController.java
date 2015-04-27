package com.projectsandbox.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 27/04/15.
 */
@Controller
public class HomeController extends BaseController
{
    @RequestMapping(value = { "/, /home" })
    public ModelAndView home()
    {
        ModelAndView mv = create("home");

        System.out.println("call made");

        return mv;
    }
}
