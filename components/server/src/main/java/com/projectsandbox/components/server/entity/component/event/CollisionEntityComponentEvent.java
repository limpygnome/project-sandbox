package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface CollisionEntityComponentEvent extends ComponentEvent
{

    void eventCollisionEntity(Controller controller, Entity entity, Entity entityOther, CollisionResult result);

}
