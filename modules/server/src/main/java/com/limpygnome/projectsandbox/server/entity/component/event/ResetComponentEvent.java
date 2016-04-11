package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface ResetComponentEvent
{

    void eventReset(Controller controller, Entity entity);

}
