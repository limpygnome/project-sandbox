package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.entity.death.AbstractKiller;

/**
 * Created by limpygnome on 12/04/16.
 */
public interface DeathComponentEvent extends ComponentEvent
{

    void eventDeath(Controller controller, Entity entity, AbstractKiller killer);

}
