package com.limpygnome.projectsandbox.server.ents.types.pickups;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.ents.physics.collisions.CollisionResult;
import com.limpygnome.projectsandbox.server.ents.respawn.RespawnProperties;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

import static com.limpygnome.projectsandbox.server.constants.entities.pickups.AbstractPickupConstants.*;

/**
 * Created by limpygnome on 06/07/15.
 */
public abstract class AbstractPickup extends Entity
{
    private long respawnDelay;

    public AbstractPickup(long respawnDelay)
    {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);

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
            // Pickup has been redeemed, now to respawn in a period of time
            controller.respawnManager.respawn(new RespawnProperties(controller, this, respawnDelay, true));
        }
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return new PlayerInfo[0];
    }
}
