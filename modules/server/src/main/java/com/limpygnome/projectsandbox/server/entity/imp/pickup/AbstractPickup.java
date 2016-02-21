package com.limpygnome.projectsandbox.server.entity.imp.pickup;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;

/**
 * Created by limpygnome on 06/07/15.
 */
public abstract class AbstractPickup extends Entity
{
    private long respawnDelay;

    public AbstractPickup(WorldMap map, short width, short height, long respawnDelay)
    {
        super(map, width, height);

        this.respawnDelay = respawnDelay;
        this.physicsIntangible = true;

        setGodmode();
    }

    public abstract boolean applyPickup(Controller controller, Entity entity);

    @Override
    public void eventHandleCollision(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        // Call implementation to handle pickup
        if (applyPickup(controller, entOther))
        {
            // Set entity to dead
            setDead(true);

            // Pickup has been redeemed, now to respawn in a period of time
            map.respawnManager.respawn(new EntityPendingRespawn(controller, this, respawnDelay));
        }
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[0];
    }
}
