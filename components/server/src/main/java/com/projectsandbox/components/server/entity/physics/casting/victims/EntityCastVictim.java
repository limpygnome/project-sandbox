package com.projectsandbox.components.server.entity.physics.casting.victims;

import com.projectsandbox.components.server.entity.Entity;

/**
 * Created by limpygnome on 07/05/15.
 */
public class EntityCastVictim extends AbstractCastVictim
{
    public Entity entity;

    public EntityCastVictim(Entity entity)
    {
        this.entity = entity;
    }

    @Override
    public String toString()
    {
        return "[ent victim - id: " + entity.id + ", type: " + entity.getClass().getName() + "]";
    }
}
