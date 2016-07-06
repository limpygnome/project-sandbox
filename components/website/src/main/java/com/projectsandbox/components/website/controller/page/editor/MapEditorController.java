package com.projectsandbox.components.website.controller.page.editor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by limpygnome on 22/06/16.
 */
@Controller
@RequestMapping(value = "/map-editor")
public class MapEditorController
{

    // TODO: add auth filter around this area...

    private final static Logger LOG = LogManager.getLogger(MapEditorController.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView editor()
    {
        ModelAndView modelAndView = new ModelAndView("map-editor/main");
        return modelAndView;
    }

}
