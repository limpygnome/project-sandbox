package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.entity.component.ComponentEvent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;

/**
 * Created by limpygnome on 14/04/16.
 */
public interface FetchVelocityComponentEvent extends ComponentEvent
{

    Vector2 getVelocity();

}
