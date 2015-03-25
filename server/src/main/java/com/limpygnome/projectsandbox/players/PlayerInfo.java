package com.limpygnome.projectsandbox.players;

import com.limpygnome.projectsandbox.ents.Entity;
import org.java_websocket.WebSocket;

/**
 *
 * @author limpygnome
 */
public class PlayerInfo
{
    public enum PlayerKey
    {
        MovementUp(1),
        MovementLeft(2),
        MovementDown(4),
        MovementRight(8),
        Action(16)
        ;
        
        public final int FLAG;
        
        private PlayerKey(int flag)
        {
            this.FLAG = flag;
        }
    }
    
    public byte keys;
    
    /**
     * The player's web socket.
     */
    public WebSocket socket;
    
    /**
     * The player's current entity.
     */
    public Entity entity;
    
    public PlayerInfo(WebSocket socket)
    {
        this.keys = 0;
        this.socket = socket;
        this.entity = null;
    }
    
    public boolean isKeyDown(PlayerKey key)
    {
        return (keys & key.FLAG) == key.FLAG;
    }
    
    public void setKey(PlayerKey key, boolean down)
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
