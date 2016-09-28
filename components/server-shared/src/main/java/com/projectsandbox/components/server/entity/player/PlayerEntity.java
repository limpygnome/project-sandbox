package com.projectsandbox.components.server.entity.player;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.event.PlayerInfoKeyDownComponentEvent;
import com.projectsandbox.components.server.inventory.Inventory;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;
import com.projectsandbox.components.server.world.spawn.Spawn;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


    public PlayerEntity(short width, short height)
    {
        super(width, height);

        players = null;
        inventories = null;
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

        if (players == null)
        {
            throw new RuntimeException("No maximum players defined, make call to setMaxPlayers");
        }

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

    public synchronized void setMaxPlayers(int maxPlayers)
    {
        // Check not already defined
        if (players != null)
        {
            throw new RuntimeException("Maximum players already setup, cannot be called multiple times");
        }

        // Setup empty arrays for players and their inventories
        this.players = new PlayerInfo[maxPlayers];
        this.inventories = new Inventory[maxPlayers];
    }

    public synchronized void setPlayer(PlayerInfo player, int index)
    {
        // Ensure players array setup; may not be case after deserialization...
        if (players == null)
        {
            if (inventories == null)
            {
                throw new RuntimeException("Need to call setMaxPlayers - type: " + getClass().getSimpleName());
            }

            // Must have just respawned, hence just create new empty players
            players = new PlayerInfo[inventories.length];
        }

        // Check index in range
        if (index >= players.length)
        {
            throw new RuntimeException("Attempted to put player in seat index " + index + ", but only " + players.length + " seats");
        }

        // Check another player is not already assigned
        if (players[index] != null)
        {
            throw new RuntimeException("Attempted to assign player to seat index " + index +", but already player");
        }

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

    /**
     * Forwards keys to inventory and to components (as events).
     */
    public void eventPlayerInfoKeyChange(Controller controller, PlayerInfo playerInfo, PlayerKeys key, boolean isKeyDown)
    {
        // Fetch index of player
        int playerIndex = getPlayerIndex(playerInfo);

        // Forward to inventory
        Inventory inventory = inventories[playerIndex];

        if (inventory != null)
        {
            inventory.eventPlayerInfoKeyChange(controller, playerInfo, key, playerIndex, isKeyDown);
        }

        // Forward as component event
        Set<PlayerInfoKeyDownComponentEvent> eventHandlers = this.components.fetch(PlayerInfoKeyDownComponentEvent.class);

        for (PlayerInfoKeyDownComponentEvent event : eventHandlers)
        {
            event.eventPlayerInfoKeyChange(controller, playerInfo, key, playerIndex, isKeyDown);
        }
    }

}
