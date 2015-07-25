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
public class ForbiddenController extends BaseController
{
    @RequestMapping(value = "/forbidden")
    public ModelAndView pageNotFound(HttpServletResponse response)
    {
        response.setStatus(403);

        // TODO: extensive logging on the user, most likely attacker (user agent, ip, cookies, params, etc)
        // TODO: use separate log file

        return createMV("main/forbidden", "forbidden");
    }

}
