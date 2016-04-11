package com.limpygnome.projectsandbox.server.entity.component.event;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;

/**
 * Created by limpygnome on 11/04/16.
 */
public interface CollisionEntityComponentEvent
{

    void eventHandleCollisionEntity(Controller controller, Entity entity, Entity entityOther, CollisionResult result);

}
