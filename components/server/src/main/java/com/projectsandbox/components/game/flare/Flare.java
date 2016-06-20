package com.projectsandbox.components.game.flare;

import com.projectsandbox.components.game.OwnershipComponent;
import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.physics.collisions.map.CollisionMapResult;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

/**
 * A flare is designed to exist only for a small period of time, with low health, removed on collision with another
 * entity or after a period of time. This is useful as defence against weapons such as rockets.
 */
@EntityType(typeId = 610, typeName = "world/flare")
public class Flare extends Entity
{
    private long gameTimeExpires;
    private long lifespan;

    public Flare(WorldMap map, long lifespan)
    {
        super(map, (short) 16, (short) 16);

        setMaxHealth(1);

        this.lifespan = lifespan;
        this.physicsIntangible = true;
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        this.gameTimeExpires = controller.gameTime() + lifespan;
    }

    @Override
    public synchronized void eventLogic(Controller controller)
    {
        super.eventLogic(controller);

        // Check if entity lived beyond lifespan...
        long gameTime = controller.gameTime();

        if (gameTime >= gameTimeExpires)
        {
            remove();
        }
    }

    @Override
    public synchronized void eventCollisionEntity(Controller controller, Entity entityCollider, Entity entityVictim, Entity entityOther, CollisionResult result)
    {
        super.eventCollisionEntity(controller, entityCollider, entityVictim, entityOther, result);

        // Remove this entity if collided entity not owner
        OwnershipComponent ownershipComponent = (OwnershipComponent) components.fetchComponent(OwnershipComponent.class);
        if (!ownershipComponent.isOwnedBySamePlayer(entityOther))
        {
            remove();
        }
    }

    @Override
    public synchronized void eventCollisionMap(Controller controller, CollisionMapResult collisionMapResult)
    {
        super.eventCollisionMap(controller, collisionMapResult);

        // Remove this entity...
        remove();
    }

    @Override
    public String friendlyName()
    {
        return "Flare";
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return PlayerInfo.EMPTY_PLAYERS;
    }

}
