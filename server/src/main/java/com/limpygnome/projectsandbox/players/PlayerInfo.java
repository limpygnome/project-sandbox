package com.limpygnome.projectsandbox.players;

import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.ents.Faction;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public class PlayerInfo
{
    /**
     * The current session tied to the player; null if no session assigned.
     */
    public Session session;
    
    /**
     * The keys currently held down by the player.
     */
    public short keys;
    
    /**
     * The player's web socket.
     */
    public WebSocket socket;
    
    /**
     * Default faction for all new ents.
     */
    public Faction defaultFaction;
    
    /**
     * The player's current entity.
     */
    public Entity entity;
    
    public PlayerInfo(WebSocket socket, Session session)
    {
        this.keys = 0;
        this.socket = socket;
        this.entity = null;
        this.defaultFaction = Faction.NONE;
        this.session = session;
    }
    
    public boolean isConnected()
    {
        return socket != null && socket.isOpen();
    }
    
    public boolean isKeyDown(PlayerKeys key)
    {
        return (keys & key.FLAG) == key.FLAG;
    }
    
    public void setKey(PlayerKeys key, boolean down)
    {
        if (down)
        {
            keys |= key.FLAG;
        }
        else
        {
            keys &= ~key.FLAG;
        }
    }
}
