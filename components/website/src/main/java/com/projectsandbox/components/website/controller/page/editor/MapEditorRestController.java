package com.projectsandbox.components.website.controller.page.editor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by limpygnome on 22/06/16.
 */
@RequestMapping(value = "/map-editor/data")
public class MapEditorRestController
{

    @RequestMapping(value = "fetch", method = RequestMethod.GET)
    public void dataFetch()
    {
    }

    @RequestMapping(value = "persist", method = RequestMethod.POST)
    public void dataPersist()
    {
    }

}
