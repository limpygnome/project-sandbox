package com.limpygnome.projectsandbox.website.controllers;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base controller of which all controllers should extend.
 *
 * Allows for common attributes to be defined.
 */
@Controller
public class BaseController
{
    public static final String MODEL_ATTRIB_TITLE = "title";

    protected ModelAndView createMV(String page)
    {
        return createMV(page, page);
    }

    protected ModelAndView createMV(String page, String title)
    {
        ModelAndView mv = new ModelAndView();

        mv.setViewName(page);

        if (title != null)
        {
            mv.addObject(MODEL_ATTRIB_TITLE, title);
        }

        return mv;
    }

    @ModelAttribute("title")
    public String getDefaultTitle() {
        return "Undefined Title";
    }

    @ModelAttribute("copyright_year")
    public String getCopyrightYear() {
        DateTime dt = DateTime.now();
        return String.valueOf(dt.getYear());
    }
}
