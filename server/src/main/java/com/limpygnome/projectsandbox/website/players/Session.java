package com.limpygnome.projectsandbox.website.players;

import java.util.UUID;

/**
 * A JPA model representing a session for a player.
 * 
 * This is loaded and cached in {@link PlayerInfo} when a user sends
 * their session packet, when joining.
 * 
 * @author limpygnome
 */
public class Session
{
    public UUID sessionId;
    
    public String displayName;
    public long joinTimestamp;
    
    public Session()
    {
        this(null, null, System.currentTimeMillis());
    }
    
    public Session(UUID sessionId, String displayName, long joinTimestamp)
    {
        this.sessionId = sessionId;
        this.displayName = displayName;
        this.joinTimestamp = joinTimestamp;
    }
}
