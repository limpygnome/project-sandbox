package com.limpygnome.projectsandbox.server.players;

import com.limpygnome.projectsandbox.server.players.enums.PlayerKeys;
import com.limpygnome.projectsandbox.server.ents.Entity;
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
    public short defaultFaction;
    
    /**
     * The player's current entity.
     */
    public Entity entity;
    
    public PlayerInfo(WebSocket socket, Session session)
    {
        this.keys = 0;
        this.socket = socket;
        this.entity = null;
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