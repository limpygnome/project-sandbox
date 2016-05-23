package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;

/**
 * Created by limpygnome on 14/04/16.
 */
public interface FetchVelocityComponentEvent extends ComponentEvent
{

    Vector2 getVelocity();

}
