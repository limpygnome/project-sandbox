package com.limpygnome.projectsandbox.website.controllers.special;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import com.limpygnome.projectsandbox.website.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by limpygnome on 20/04/2015.
 */
@Controller
public class PageNotFoundController extends BaseController
{
    @RequestMapping(value = "/page-not-found")
    public ModelAndView pageNotFound(HttpServletResponse response)
    {
        response.setStatus(404);

        return createMV("main/page-not-found", "page not found");
    }

}
