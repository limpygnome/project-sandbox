package com.limpygnome.projectsandbox.server.entity.respawn;

import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

/**
 * Created by limpygnome on 21/07/15.
 */
public class RespawnData
{
    public final Spawn spawn;
    public final PendingRespawn pendingRespawn;

    public RespawnData(Spawn spawn, PendingRespawn pendingRespawn)
    {
        this.spawn = spawn;
        this.pendingRespawn = pendingRespawn;
    }
}
