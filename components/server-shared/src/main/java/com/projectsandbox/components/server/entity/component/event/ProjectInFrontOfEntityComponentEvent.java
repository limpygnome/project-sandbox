package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.entity.physics.Vector2;

/**
 * Created by limpygnome on 23/05/16.
 */
public interface ProjectInFrontOfEntityComponentEvent extends ComponentEvent
{

    void projectInFrontOfEntity(Entity entity, Entity parent, float spacing, Vector2 newPosition);

}
