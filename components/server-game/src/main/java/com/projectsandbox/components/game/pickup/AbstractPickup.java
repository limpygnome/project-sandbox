package com.projectsandbox.components.game.pickup;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.player.PlayerInfo;

/**
 * Created by limpygnome on 06/07/15.
 */
public abstract class AbstractPickup extends Entity
{
    private long respawnDelay;

    public AbstractPickup(short width, short height)
    {
        super(width, height);

        this.physicsIntangible = true;

        setGodmode();
    }

    public abstract boolean applyPickup(Controller controller, Entity entity);

    protected void setRespawnDelay(long respawnDelay)
    {
        this.respawnDelay = respawnDelay;
    }

    @Override
    public void eventCollisionEntity(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        // Call implementation to handle pickup
        if (applyPickup(controller, entOther))
        {
            // Pickup has been redeemed, now to respawn in a period of time
            controller.respawnManager.respawn(new EntityPendingRespawn(controller, map, this, respawnDelay, false));
        }
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[0];
    }
}
