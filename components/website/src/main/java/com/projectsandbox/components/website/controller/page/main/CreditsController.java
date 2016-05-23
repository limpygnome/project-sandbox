package com.projectsandbox.components.website.controller.page.main;

import com.projectsandbox.components.website.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 21/07/15.
 */
@Controller
public class CreditsController extends BaseController
{

    @RequestMapping("/credits")
    public ModelAndView credits()
    {
        return createMV("main/credits", "credits");
    }

}
