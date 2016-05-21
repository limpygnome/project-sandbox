package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.component.ComponentEvent;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface ResetComponentEvent extends ComponentEvent
{

    void eventReset(Controller controller, Entity entity);

}
