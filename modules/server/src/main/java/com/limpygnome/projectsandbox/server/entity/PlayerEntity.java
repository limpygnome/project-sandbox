package com.limpygnome.projectsandbox.server.entity;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.inventory.Inventory;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerEntity extends Entity
{
    private final static Logger LOG = LogManager.getLogger(PlayerEntity.class);

    /* Used to indicate if the entity should be persisted to the player's session when leaving */
    private PlayerInfo owner;

    /* All of the players which are using this entity. */
    private PlayerInfo[] players;

    /* The index should match the players index i.e. players[0] controls inventories[0]. */
    private Inventory[] inventories;

    /**
     * Creates a new instance.
     *
     * The 'players' parameter should at least be an empty array of players able to use the entity, or an array of
     * actual players using it, or a mix. The size of the array will control how many players can simultaneously
     * use the entity.
     *
     * The 'inventories' parameter should at least be an empty array of possible inventories, or an array of actual
     * inventories to use, or a mix of both.
     *
     * As an example for a vehicle, you could specify an empty array of size four for 'players', along with an
     * 'inventories' array of size one with a main weapon in position one (zero indexed), so that the first passenger
     * has a weapon.
     *
     * @param map map to which the entity belongs
     * @param width width
     * @param height height
     * @param players sets up the players
     * @param inventories sets up the inventories
     */
    public PlayerEntity(WorldMap map, short width, short height, PlayerInfo[] players, Inventory[] inventories)
    {
        super(map, width, height);

        setPlayers(players);
        setInventories(inventories);
    }

    /**
     * Refer to constructor.
     *
     * @param players players
     */
    public synchronized void setPlayers(PlayerInfo[] players)
    {
        // TODO: do we need to unbind old players
        this.persistToSession = false;
        this.players = players;

        // Update inventory ownership
        if (inventories != null)
        {
            for (int i = 0; i < players.length; i++)
            {
                updateInventoryOwnership(i);
            }
        }
    }

    public synchronized void setPlayer(PlayerInfo player, int index)
    {
        // TODO: do we need to unbind old player?

        // Reset persistence flag if main player changes
        if (index == 0)
        {
            persistToSession = false;
        }

        this.players[index] = player;
        updateInventoryOwnership(index);
    }

    /**
     * Refer to constructor.
     *
     * @param inventories inventories
     */
    public synchronized void setInventories(Inventory[] inventories)
    {
        // TODO: do we need to unbind old inventories?
        this.inventories = inventories;

        if (inventories != null)
        {
            // Make sure all players are owners, if available...
            for (int i = 0; i < inventories.length; i++)
            {
                updateInventoryOwnership(i);
            }
        }
    }

    private synchronized void updateInventoryOwnership(int index)
    {
        PlayerInfo playerInfo = (players != null && index < players.length ? players[index] : null);
        Inventory inventory = (inventories != null && index < inventories.length ? inventories[index] : null);

        if (playerInfo != null && inventory != null)
        {
            inventory.setOwner(playerInfo);
        }
    }

    @Override
    public synchronized void logic(Controller controller)
    {
        super.logic(controller);

        if (inventories != null)
        {
            Inventory inventory;
            for (int i = 0; i < inventories.length; i++)
            {
                inventory = inventories[i];

                if (inventory != null)
                {
                    inventory.logic(controller);
                }
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
    public synchronized void eventPendingDeleted(Controller controller)
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
    public synchronized String friendlyName()
    {
        PlayerInfo playerInfo = getPlayer();

        if (playerInfo != null)
        {
            return playerInfo.session.getNickname();
        }

        return "Unknown";
    }

    public synchronized PlayerInfo getPlayer()
    {
        return players != null && players.length >= 1 ? players[0] : null;
    }

    @Override
    public synchronized PlayerInfo[] getPlayers()
    {
        return players;
    }

    @Override
    public synchronized Inventory retrieveInventory(PlayerInfo playerInfo)
    {
        if (inventories == null)
        {
            LOG.warn("Unable to retrieve inventory, inventories not set / null - ply id: {}, ent id; {}", playerInfo.playerId, id);
        }
        else
        {
            int index = getPlayerIndex(playerInfo);

            if (index != -1 && index < inventories.length)
            {
                return inventories[index];
            }
        }

        return null;
    }

    public synchronized void setInventory(int index, Inventory inventory)
    {
        if (index < inventories.length)
        {
            inventories[index] = inventory;
            updateInventoryOwnership(index);
        }
        else
        {
            LOG.warn("Unable to set inventory, inventories not large enough - index: {}, ent id; {}", index, id);
        }
    }

    private synchronized int getPlayerIndex(PlayerInfo playerInfo)
    {
        if (players == null)
        {
            LOG.warn("Unable to find player index when players not set / null - ply id: {}", playerInfo.playerId);
        }
        else if (playerInfo != null)
        {
            for (int i = 0; i < players.length; i++)
            {
                if (playerInfo.equals(players[i]))
                {
                    return i;
                }
            }
        }

        return -1;
    }

}
