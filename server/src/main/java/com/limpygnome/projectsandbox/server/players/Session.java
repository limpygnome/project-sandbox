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
    /**
     * The UUID for the current user's web session.
     */
    public UUID sessionId;

    /**
     * Registered player ID (UUID); null if guest.
     */
    public UUID registeredPlayerId;

    /**
     * The display name of the user.
     */
    public String displayName;

    /**
     * The timestamp at which the player joined.
     */
    public long joinTimestamp;

    /**
     * Persisted player data.
     */
    public PlayerData playerData;

    /**
     * Metrics regarding the user's current session.
     */
    public SessionMetrics metrics;

    private Session(UUID sessionId, UUID registeredPlayerId, String displayName, long joinTimestamp)
    {
        this.sessionId = sessionId;
        this.registeredPlayerId = registeredPlayerId;
        this.displayName = displayName;
        this.joinTimestamp = joinTimestamp;
        this.playerData = null;
        this.metrics = new SessionMetrics();
    }

    public static Session load(UUID sessionId)
    {
        int rand = new Random(System.currentTimeMillis()).nextInt(100);

        // Load session data
        // TODO: remove stub and load session from DB
        // TODO: check name is not in use, else append number
        Session session = new Session(sessionId, null, "unnamed" + rand, System.currentTimeMillis());

        // Load player data
        session.playerData = PlayerData.load(session.registeredPlayerId);

        return session;
    }
}
