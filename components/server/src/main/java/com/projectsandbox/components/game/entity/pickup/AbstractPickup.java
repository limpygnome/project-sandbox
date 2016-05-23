package com.projectsandbox.components.game.entity.pickup;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

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
    public void eventCollisionEntity(Controller controller, Entity entCollider, Entity entVictim, Entity entOther, CollisionResult result)
    {
        // Call implementation to handle pickup
        if (applyPickup(controller, entOther))
        {
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
