package com.limpygnome.projectsandbox.server.players;

import java.util.Random;
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
    // The UUID for the current user's web session.
    public UUID sessionId;
    // Null for a guest user.
    public UUID registeredPlayerId;
    public String displayName;
    public long joinTimestamp;
    public PlayerData playerData;

    public SessionMetrics metrics;

    private Session(UUID sessionId, UUID registeredPlayerId, String displayName, long joinTimestamp)
    {
        this.sessionId = sessionId;
        this.registeredPlayerId = registeredPlayerId;
        this.displayName = displayName;
        this.joinTimestamp = joinTimestamp;
    }

    public static Session load(UUID sessionId)
    {
        // TODO: load player data
        // TODO: remove stub and load from DB
        int rand = new Random(System.currentTimeMillis()).nextInt(100);

        // Load session data
        Session session = new Session(sessionId, null, "unnamed" + rand, System.currentTimeMillis());

        // Load player data
        session.playerData = PlayerData.load(session.registeredPlayerId);

        return session;
    }
}
