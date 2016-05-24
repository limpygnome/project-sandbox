package com.projectsandbox.components.server.entity.component.imp;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.PlayerEntity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.CollisionEntityComponentEvent;
import com.projectsandbox.components.server.entity.component.event.DeathComponentEvent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.death.AbstractKiller;
import com.projectsandbox.components.server.entity.physics.Vector2;
import com.projectsandbox.components.server.entity.physics.collisions.CollisionResult;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

/**
 * Created by limpygnome on 12/04/16.
 */
public class PlayerEjectionComponent implements EntityComponent, LogicComponentEvent, DeathComponentEvent, CollisionEntityComponentEvent
{

    /**
     * The space between a vehicle and an ejected player.
     */
    public static final float EJECT_SPACING = 2.0f;

    protected Vector2[] playerEjectPositions;

    // Indicates that the driver, player zero, was spawned in this vehicle - thus respawn vehicle with player on death
    protected boolean flagDriverSpawned;

    public PlayerEjectionComponent(PlayerEntity playerEntity, Vector2[] playerEjectPositions)
    {
        if (playerEjectPositions == null || playerEjectPositions.length == 0 || playerEjectPositions[0] == null)
        {
            throw new IllegalArgumentException("Player ejection positions must have at least one non-null item");
        }
        else if (playerEjectPositions.length < 1)
        {
            throw new IllegalArgumentException("Must be at least one eject position");
        }

        this.playerEjectPositions = playerEjectPositions;

        // Check if spawned entity has player/driver; they'll need respawn later if so...
        this.flagDriverSpawned = (playerEntity.getPlayer() != null);
    }

    @Override
    public synchronized void eventLogic(Controller controller, Entity entity)
    {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        PlayerInfo playerInfoDriver = playerEntity.getPlayer();
        PlayerInfo[] players = entity.getPlayers();

        // Check if players want to get out / are still connected
        PlayerInfo playerInfo;

        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];

            if (playerInfo != null)
            {
                if (!playerInfo.isConnected())
                {
                    // Free the seat...
                    players[i] = null;
                }
                else if (playerInfo.isKeyDown(PlayerKeys.Action))
                {
                    // Set action key to handled
                    playerInfo.setKey(PlayerKeys.Action, false);

                    // Fetch ejection seat
                    Vector2 ejectPosition;

                    if (playerEjectPositions.length >= i)
                    {
                        ejectPosition = playerEjectPositions[0];
                    }
                    else
                    {
                        ejectPosition = playerEjectPositions[i];
                    }

                    // Eject player from the vehicle
                    playerEject(controller, entity, playerInfo, ejectPosition);

                    // Free-up the space
                    players[i] = null;

                    // Reset spawn flag if driver
                    if (playerInfo == playerInfoDriver)
                    {
                        flagDriverSpawned = false;
                    }

                    // NOTE: future event hook could go here...
                }
            }
        }
    }

    private synchronized void playerEject(Controller controller, Entity entity, PlayerInfo playerInfo, Vector2 ejectPosition)
    {
        WorldMap worldMap = entity.map;

        // Offset position so that the player exits to the left of the vehicle
        Vector2 plyPos = ejectPosition.clone();

        // Create new player ent in position of vehicle
        Entity entityPlayer = controller.playerEntityService.createPlayer(worldMap, playerInfo);

        // Add player to pos offset
        float playerOffsetX = playerEjectVectorPos(ejectPosition.x, entityPlayer.width / 2.0f);
        float playerOffsetY = playerEjectVectorPos(ejectPosition.y, entityPlayer.height / 2.0f);
        plyPos.add(playerOffsetX, playerOffsetY);

        // Rotate pos to align with vehicle
        plyPos.rotate(0.0f, 0.0f, entity.rotation);

        // Add pos of vehicle to pos
        plyPos.add(entity.positionNew);

        // Spawn player
        worldMap.respawnManager.respawn(new PositionPendingRespawn(
                controller,
                entityPlayer,
                new Spawn(plyPos.x, plyPos.y, entity.rotation)
        ));
    }

    private synchronized float playerEjectVectorPos(float coord, float value)
    {
        if (coord == 0)
        {
            return 0.0f;
        }
        else if (coord < 0)
        {
            return (value * -1.0f) + EJECT_SPACING;
        }
        else
        {
            return value + EJECT_SPACING;
        }
    }

    @Override
    public void eventDeath(Controller controller, Entity entity, AbstractKiller killer)
    {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        WorldMap worldMap = entity.map;

        // Respawn players in vehicle
        PlayerInfo[] players = playerEntity.getPlayers();
        PlayerInfo playerInfo;

        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];

            if (playerInfo != null && !(flagDriverSpawned && i == 0))
            {
                // Create and respawn player
                Entity entityPlayer = controller.playerEntityService.createPlayer(worldMap, playerInfo);
                worldMap.respawnManager.respawn(new EntityPendingRespawn(controller, entityPlayer));

                // Set seat to empty
                players[i] = null;
            }
        }
    }

    @Override
    public synchronized void eventCollisionEntity(Controller controller, Entity entity, Entity entityOther, CollisionResult result)
    {
        // Check if player
        if (entityOther instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            PlayerEntity playerEntityOther = (PlayerEntity) entityOther;

            // Check if they're holding down action key to get in vehicle
            PlayerInfo playerInfo = playerEntityOther.getPlayer();

            if (playerInfo != null && playerInfo.isKeyDown(PlayerKeys.Action))
            {
                // Set action key off/handled
                playerInfo.setKey(PlayerKeys.Action, false);

                // Check for next available seat
                PlayerInfo[] players = playerEntity.getPlayers();
                PlayerInfo plyInSeat;

                for (int i = 0; i < players.length; i++)
                {
                    plyInSeat = players[i];

                    if (plyInSeat == null || !plyInSeat.isConnected())
                    {
                        // Set the player to use this entity
                        controller.playerService.setPlayerEntity(playerInfo, entity);

                        // Add as passenger
                        playerEntity.setPlayer(playerInfo, i);

                        // NOTE: could add hook here for vehicle entry in future
                        break;
                    }
                }
            }
        }
    }

}
