package com.limpygnome.projectsandbox.server.players;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.ents.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.ents.types.living.Player;
import com.limpygnome.projectsandbox.server.packets.types.players.global.PlayerKilledOutboundPacket;
import com.limpygnome.projectsandbox.server.players.enums.PlayerKeys;
import com.limpygnome.projectsandbox.server.ents.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

import java.io.IOException;

/**
 * @author limpygnome
 */
public class PlayerInfo
{
    private final static Logger LOG = LogManager.getLogger(PlayerInfo.class);

    /**
     * The identifier used for the player during their current session.
     */
    public final short playerId;

    /**
     * The current session tied to the player; null if no session assigned.
     * <p>
     * Note: this should NEVER reach other players, since this is the temp token to identify the player for the duration of the session.
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
     * TODO: consider removal or move into player data
     * <p>
     * Default faction for all new ents.
     */
    public short defaultFaction;

    /**
     * The player's current entity.
     */
    public Entity entity;

    public PlayerInfo(WebSocket socket, Session session, short playerId)
    {
        this.keys = 0;
        this.socket = socket;
        this.entity = null;
        this.session = session;
        this.playerId = playerId;
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
        } else
        {
            keys &= ~key.FLAG;
        }
    }

    public void eventPlayerKilled(Controller controller, AbstractKiller death, PlayerInfo[] playerInfoKillers)
    {
        try
        {
            // Update metrics
            session.metrics.incrementDeaths();

            // Build death packet
            PlayerKilledOutboundPacket packet = new PlayerKilledOutboundPacket();
            packet.writePlayerKilled(death, this);

            // Broadcast to all players
            controller.playerManager.broadcast(packet);

            LOG.info("Player killed - ply id: {}, killer: {}", playerId, death);
        }
        catch (IOException ex)
        {
            LOG.error("Failed to handle player being killed", ex);
        }
    }

    public void eventPlayerKill(Controller controller, AbstractKiller death, PlayerInfo[] playerInfoVictims)
    {
        // Update kills
        session.metrics.incrementKills();

        // Update score
        int score = death.computeScore();
        if (score > 0)
        {
            session.metrics.incrementScore(score);
        }

        LOG.debug("Player inflicted death - ply id: {}, killer: {}", playerId, death);
    }

}