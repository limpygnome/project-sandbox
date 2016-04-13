package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.component.ComponentEvent;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResultMap;

/**
 * Created by limpygnome on 12/04/16.
 */
public interface CollisionMapComponentEvent extends ComponentEvent
{

    void eventCollisionMap(Controller controller, Entity entity, CollisionResultMap collisionResultMap);

}
