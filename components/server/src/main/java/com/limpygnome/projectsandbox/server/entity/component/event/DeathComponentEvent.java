package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.component.ComponentEvent;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;

/**
 * Created by limpygnome on 12/04/16.
 */
public interface DeathComponentEvent extends ComponentEvent
{

    void eventDeath(Controller controller, Entity entity, AbstractKiller killer);

}
