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
    public static final String MODEL_ATTRIB_CONTENT_CLASS = "contentClass";

    protected ModelAndView createMV(String page)
    {
        return createMV(page, page);
    }

    protected ModelAndView createMV(String page, String title)
    {
        return createMV(page, title, "content");
    }

    protected ModelAndView createMV(String page, String title,
                                    String contentClass)
    {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName(page);

        // Add page title
        if (title != null)
        {
            modelAndView.addObject(MODEL_ATTRIB_TITLE, title);
        }

        // Add CSS classes
        if (contentClass != null)
        {
            modelAndView.addObject(MODEL_ATTRIB_CONTENT_CLASS, contentClass);
        }

        return modelAndView;
    }

    /**
     * Defines a default title.
     *
     * @return Default title
     */
    @ModelAttribute("title")
    public String getDefaultTitle() {
        return "Undefined Title";
    }

    /**
     * The current year, for copyright.
     *
     * @return Copyright year
     */
    @ModelAttribute("copyright_year")
    public String getCopyrightYear() {
        DateTime dt = DateTime.now();
        return String.valueOf(dt.getYear());
    }

}
