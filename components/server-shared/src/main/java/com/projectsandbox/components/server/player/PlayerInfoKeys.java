package com.projectsandbox.components.server.player;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by limpygnome on 28/09/16.
 */
public class PlayerInfoKeys
{
    private final static Logger LOG = LogManager.getLogger(PlayerInfoKeys.class);

    // Reference to player
    private PlayerInfo playerInfo;

    /*
     * The keys currently held down by the player.
     *
     * @see PlayerInfoKeys.FLAG
     */
    private short keys;

    // Observers registered for key down/up events
    private List<PlayerKeysObserver> observers;

    public PlayerInfoKeys(PlayerInfo playerInfo)
    {
        this.playerInfo = playerInfo;
        this.keys = 0;
        this.observers = new LinkedList<>();
    }

    public synchronized void setKeys(Controller controller, short keys)
    {
        short oldKeys = this.keys;

        // Update current keys
        this.keys = keys;

        // Determine if the state of each key has changed
        boolean oldKeyDown;
        boolean newKeyDown;

        for (PlayerKeys key : PlayerKeys.values())
        {
            oldKeyDown = (oldKeys & key.FLAG) == key.FLAG;
            newKeyDown = (keys & key.FLAG) == key.FLAG;

            if (oldKeyDown != newKeyDown)
            {
                // Raise with observers
                keyEvent(controller, key, newKeyDown);
            }
        }
    }

    public synchronized void setKey(Controller controller, PlayerKeys key, boolean down)
    {
        // Change keys
        if (down)
        {
            keys |= key.FLAG;
        }
        else
        {
            keys &= ~key.FLAG;
        }

        // Inform observers
        keyEvent(controller, key, down);
    }

    /**
     * Resets all the keys to up / not pressed.
     */
    public synchronized void reset(Controller controller)
    {
        setKeys(controller, (short) 0);
    }

    private void keyEvent(Controller controller, PlayerKeys key, boolean isKeyDown)
    {
        // Hand to attached entity of player
        Entity entity = playerInfo.entity;

        if (entity != null && entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            playerEntity.eventPlayerInfoKeyChange(controller, playerInfo, key, isKeyDown);
        }
    }

    public boolean isKeyDown(PlayerKeys key)
    {
        return (keys & key.FLAG) == key.FLAG;
    }

}
