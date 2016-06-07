package com.projectsandbox.components.server.entity.player;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public abstract class PlayerEntity extends Entity
{
    private final static Logger LOG = LogManager.getLogger(PlayerEntity.class);

    /*
        Used to indicate if the entity should be persisted to the player's session when leaving.

        The owner will be the player at the zero index/position.
    */
    private transient boolean persistToSession;

    /*
        A flag used by the respawn manager, which changes the behaviour for respawning this entity if true.

        This is for respawning entities which have been persisted.
     */
    private transient boolean respawnPersistedPlayer;

    /* All of the players which are using this entity. */
    private transient PlayerInfo[] players;

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

        if (players == null || players.length == 0)
        {
            throw new IllegalArgumentException("Players must be defined, even if null sized array; defines number of players able to use vehicle");
        }

        setPlayers(players);
        setInventories(inventories);
    }

    private synchronized void updateInventoryOwnership(int index)
    {
        PlayerInfo playerInfo = (players != null && index < players.length ? players[index] : null);
        Inventory inventory = (inventories != null && index < inventories.length ? inventories[index] : null);

        if (playerInfo != null && inventory != null)
        {
            inventory.setOwner(playerInfo);
            inventory.setParent(this);
        }
    }

    @Override
    public synchronized void eventLogic(Controller controller)
    {
        super.eventLogic(controller);

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
                controller.playerService.setPlayerEntity(playerInfo, this);

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
                if (inventory != null)
                {
                    inventory.logic(controller);
                }
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

        return entityName();
    }

    public abstract String entityName();

    /**
     * Refer to constructor.
     *
     * @param players players
     */
    public synchronized void setPlayers(PlayerInfo[] players)
    {
        // TODO: do we need to unbind old players?
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
        if (index == 0 && player != players[0])
        {
            persistToSession = false;
        }

        this.players[index] = player;
        updateInventoryOwnership(index);
    }

    public synchronized void setInventory(int index, Inventory inventory)
    {
        if (index < inventories.length)
        {
            // Check if inventory already exists; if so, merge them...
            inventories[index] = inventory;
            updateInventoryOwnership(index);
        }
        else
        {
            LOG.warn("Unable to set inventory, inventories not large enough - index: {}, ent id: {}", index, id);
        }
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

    @Override
    public synchronized PlayerInfo[] getPlayers()
    {
        return players;
    }

    public synchronized PlayerInfo removePlayer(PlayerInfo playerInfo)
    {
        for (int i = 0; i < players.length; i++)
        {
            if (players[i] == playerInfo)
            {
                players[i] = null;
            }
        }

        return playerInfo;
    }

    public synchronized Inventory getInventory()
    {
        return inventories != null && inventories.length > 0 ? inventories[0] : null;
    }

    public synchronized Inventory getInventory(PlayerInfo playerInfo)
    {
        int playerIndex = getPlayerIndex(playerInfo);

        if (playerIndex != -1 && inventories != null && inventories.length > playerIndex)
        {
            return inventories[playerIndex];
        }
        else
        {
            return null;
        }
    }

    public void setPersistToSession(boolean persistToSession)
    {
        this.persistToSession = persistToSession;
    }

    /**
     * Indicates if this entity has been created from a persisted game session.
     *
     * @return true = previously persisted, false = normal entity
     */
    public boolean isRespawnPersistedPlayer()
    {
        return respawnPersistedPlayer;
    }

    /**
     * Sets a flag to indicate if to change the behaviour when respawning the player, due to the entity being created
     * from a persisted game session.
     *
     * @param respawnPersistedPlayer true = persisted, false = normal respawn
     */
    public void setRespawnPersistedPlayer(boolean respawnPersistedPlayer)
    {
        this.respawnPersistedPlayer = respawnPersistedPlayer;
    }

    /**
     * Indicates if the entity should be persisted to the driver's session.
     *
     * @param playerInfo the player to which the entity will be persisted
     * @return true = persist, false = do not persist
     */
    public boolean isPersistToSession(PlayerInfo playerInfo)
    {
        return persistToSession && players[0] == playerInfo;
    }

    /**
     * Should be invoked when a player changes their set entity to determine if to remove this entity.
     *
     * @param playerInfo the player which has been removed
     * @return true = should be removed, false = should not be removed
     */
    public boolean isRemovableOnPlayerEntChange(PlayerInfo playerInfo)
    {
        // Check if there are any players left
        for (PlayerInfo playerInfoLeft : players)
        {
            if (playerInfoLeft != null && playerInfoLeft != playerInfo)
            {
                return false;
            }
        }

        return true;
    }

}