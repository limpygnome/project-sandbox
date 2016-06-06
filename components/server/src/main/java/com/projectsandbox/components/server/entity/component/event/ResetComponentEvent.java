package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface ResetComponentEvent extends ComponentEvent
{

    void eventReset(Controller controller, Entity entity, boolean respawnAfterPersisted);

}
