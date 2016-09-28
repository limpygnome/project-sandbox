package com.projectsandbox.components.server.player;

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

    /*
     * The keys currently held down by the player.
     *
     * @see PlayerInfoKeys.FLAG
     */
    private short keys;

    // Observers registered for key down/up events
    private List<PlayerKeysObserver> observers;

    public PlayerInfoKeys()
    {
        keys = 0;
        observers = new LinkedList<>();
    }

    public void addListener(PlayerKeysObserver observer)
    {
        observers.add(observer);
    }

    public void removeListener(PlayerKeysObserver observer)
    {
        observers.remove(observer);
    }

    // TODO: fix this so it's event driven; observer pattern perhaps -> components need to hook into this
    public synchronized void setKeys(short keys)
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
                keyEvent(key, newKeyDown);
            }
        }
    }

    public synchronized void setKey(PlayerKeys key, boolean down)
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
        keyEvent(key, down);
    }

    /**
     * Resets all the keys to up / not pressed.
     */
    public synchronized void reset()
    {
        setKeys((short) 0);
    }

    private void keyEvent(PlayerKeys key, boolean down)
    {
        for (PlayerKeysObserver observer : observers)
        {
            if (down)
            {
                observer.keyDown(key);
            }
            else
            {
                observer.keyUp(key);
            }
        }
    }

//    public synchronized boolean isKeyDown(PlayerKeys key)
//    {
//        return (keys & key.FLAG) == key.FLAG;
//    }

}
