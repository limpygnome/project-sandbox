package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface LogicComponentEvent extends ComponentEvent
{

    void eventLogic(Controller controller, Entity entity);

}
