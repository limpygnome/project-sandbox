package com.limpygnome.projectsandbox.website.controllers.pages.main;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
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
        ModelAndView modelAndView = createMV("main/home", "welcome");

        return modelAndView;
    }

}
