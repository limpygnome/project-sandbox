package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;

public abstract class PlayerEntity extends Entity
{
    protected PlayerInfo[] players;

    /* The index should match the players index i.e. players[0] controls inventories[0]. */
    protected Inventory[] inventories;

    public PlayerEntity(WorldMap map, short width, short height, PlayerInfo[] players)
    {
        super(map, width, height);

        this.players = players;
        this.inventories = null;
    }

    @Override
    public synchronized void logic(Controller controller)
    {
        super.logic(controller);

        if (inventories != null)
        {
            for (Inventory inventory : inventories)
            {
                inventory.logic(controller);
            }
        }
    }

    @Override
    public synchronized void eventSpawn(Controller controller, Spawn spawn)
    {
        super.eventSpawn(controller, spawn);

        PlayerInfo playerInfo;
        Inventory inventory;

        for (int i = 0; i < players.length; i++)
        {
            playerInfo = players[i];

            if (playerInfo != null)
            {
                // Set player to use this entity
                controller.playerService.setPlayerEnt(playerInfo, this);

                // Set owner of inventory to player
                if (inventories != null && inventories[i] != null)
                {
                    inventory = inventories[i];
                    inventory.setOwner(playerInfo);
                }
            }
        }
    }

    @Override
    public void eventPendingDeleted(Controller controller)
    {
        super.eventPendingDeleted(controller);

        if (inventories != null)
        {
            for (Inventory inventory : inventories)
            {
                inventory.logic(controller);
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

    @Override
    public Inventory retrieveInventory(PlayerInfo playerInfo)
    {
    }

    public void setInventory(PlayerInfo playerInfo, Inventory inventory)
    {
    }

    private int getPlayerIndex()
    {
        for (int i = 0; i < players.length; i++)
        {
        }
    }

}
