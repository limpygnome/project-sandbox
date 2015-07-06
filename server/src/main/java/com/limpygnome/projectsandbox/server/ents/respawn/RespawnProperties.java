package com.limpygnome.projectsandbox.server.ents.respawn;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;

/**
 * Created by limpygnome on 06/07/15.
 */
public class RespawnProperties
{
    public final Entity entity;
    public final boolean alreadyActive;
    public final long gameTimeRespawn;

    public RespawnProperties(Entity entity, boolean alreadyActive, long gameTimeRespawn)
    {
        this.entity = entity;
        this.alreadyActive = alreadyActive;
        this.gameTimeRespawn = gameTimeRespawn;
    }

    public RespawnProperties(Controller controller, Entity entity, long respawnDelay, boolean alreadyActive)
    {
        this(entity, alreadyActive, controller.gameTime() + respawnDelay);
    }
}
