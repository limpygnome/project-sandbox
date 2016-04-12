package com.limpygnome.projectsandbox.server.entity.component.imp;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.component.EntityComponent;
import com.limpygnome.projectsandbox.server.entity.component.event.LogicComponentEvent;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.PositionPendingRespawn;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.PlayerKeys;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

/**
 * Created by limpygnome on 12/04/16.
 */
public class PlayerEjectionComponent implements EntityComponent, LogicComponentEvent
{
    /**
     * The space between a vehicle and an ejected player.
     */
    public static final float EJECT_SPACING = 2.0f;

    protected Vector2[] playerEjectPositions;

    public PlayerEjectionComponent(Vector2[] playerEjectPositions)
    {
        this.playerEjectPositions = playerEjectPositions;
    }

    @Override
    public synchronized void eventLogic(Controller controller, Entity entity)
    {
        // Check if players want to get out / are still connected
        PlayerInfo[] players = entity.getPlayers();
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
                    playerEject(controller, playerInfo, ejectPosition);

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

    private synchronized void playerEject(Controller controller, PlayerInfo playerInfo, Vector2 ejectPosition)
    {
        // Offset position so that the player exits to the left of the vehicle
        Vector2 plyPos = ejectPosition.clone();

        // Create new player ent in position of vehicle
        Entity entityPlayer = controller.playerEntityService.createPlayer(map, playerInfo);

        // Add player to pos offset
        float plyx = playerEjectVectorPos(ejectPosition.x, entityPlayer.width / 2.0f);
        float plyy = playerEjectVectorPos(ejectPosition.y, entityPlayer.height / 2.0f);
        plyPos = Vector2.add(plyPos, plyx, plyy);

        // Rotate pos to align with vehicle
        plyPos.rotate(0.0f, 0.0f, rotation);

        // Add pos of vehicle to pos
        plyPos = Vector2.add(plyPos, positionNew);

        // Spawn player
        map.respawnManager.respawn(new PositionPendingRespawn(
                controller,
                entityPlayer,
                new Spawn(plyPos.x, plyPos.y, rotation)
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

}
