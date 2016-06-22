package com.projectsandbox.components.website.controller.page.editor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by limpygnome on 22/06/16.
 */
@Controller
@RequestMapping(value = "/editor")
public class MapEditorController
{

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void editor()
    {
    }

}
