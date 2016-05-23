package com.projectsandbox.components.server.entity.component.event;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.ComponentEvent;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;

/**
 * Created by limpygnome on 12/04/16.
 */
public interface CollisionMapComponentEvent extends ComponentEvent
{

    void eventCollisionMap(Controller controller, Entity entity, CollisionMapResult collisionMapResult);

}
