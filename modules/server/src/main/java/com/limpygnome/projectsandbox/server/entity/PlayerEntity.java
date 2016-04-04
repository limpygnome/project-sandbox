package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

public abstract class PlayerEntity extends Entity
{
    protected PlayerInfo[] players;

    public PlayerEntity(WorldMap map, short width, short height, PlayerInfo[] players)
    {
        super(map, width, height);

        this.players = players;
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        super.eventSpawn(controller, spawn);

        for (PlayerInfo playerInfo : players)
        {
            if (playerInfo != null)
            {
                controller.playerService.setPlayerEnt(playerInfo, this);
            }
        }
    }

    @Override
    public String friendlyName()
    {
        PlayerInfo playerInfo = getPlayer();

        if (playerInfo != null)
        {
            return playerInfo.session.getNickname();
        }

        return "Unknown";
    }

    public PlayerInfo getPlayer()
    {
        return players != null && players[0] != null ? players[0] : null;
    }

    @Override
    public PlayerInfo[] getPlayers()
    {
        return players;
    }

}
