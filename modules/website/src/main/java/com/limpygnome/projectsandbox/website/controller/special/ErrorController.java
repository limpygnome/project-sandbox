package com.limpygnome.projectsandbox.website.controller.special;

import com.limpygnome.projectsandbox.website.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by limpygnome on 20/04/2015.
 */
@Controller
public class ErrorController extends BaseController
{

    @RequestMapping(value = "/error")
    public ModelAndView error(HttpServletResponse response)
    {
        response.setStatus(500);

        return createMV("main/error");
    }

}
