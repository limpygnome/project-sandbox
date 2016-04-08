package com.limpygnome.projectsandbox.server.entity.component;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;

/**
 * Interface implemented by all entity components.
 *
 * A new instance should be created for each entity, thus an instance is not a singleton. But it should not require
 * storing a reference to the entity.
 */
public interface EntityComponent
{

    void logic(Entity entity, Controller controller);

}
