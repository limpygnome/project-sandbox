package com.limpygnome.projectsandbox.server.player;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.death.AbstractKiller;
import com.limpygnome.projectsandbox.server.packet.imp.player.global.PlayerKilledOutboundPacket;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.shared.model.GameSession;
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
     * The current session tied to the player.
     */
    public GameSession session;

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

    public PlayerInfo(WebSocket socket, GameSession session, short playerId)
    {
        this.keys = 0;
        this.socket = socket;
        this.entity = null;
        this.session = session;
        this.playerId = playerId;
    }

    public synchronized boolean isConnected()
    {
        return socket != null && socket.isOpen();
    }

    public synchronized boolean isKeyDown(PlayerKeys key)
    {
        return (keys & key.FLAG) == key.FLAG;
    }

    public synchronized void setKey(PlayerKeys key, boolean down)
    {
        if (down)
        {
            keys |= key.FLAG;
        } else
        {
            keys &= ~key.FLAG;
        }
    }

    public synchronized void eventPlayerKilled(Controller controller, AbstractKiller death, PlayerInfo[] playerInfoKillers)
    {
        try
        {
            // Reset keys
            this.keys = 0;

            // Update metrics
            session.getPlayerMetrics().incrementDeaths();

            // Build death packet
            PlayerKilledOutboundPacket packet = new PlayerKilledOutboundPacket();
            packet.writePlayerKilled(death, this);

            // Broadcast to all players
            controller.playerService.broadcast(packet);

            LOG.info("Player killed - ply id: {}, killer: {}", playerId, death);
        }
        catch (IOException ex)
        {
            LOG.error("Failed to handle player being killed", ex);
        }
    }

    public synchronized void eventPlayerKill(Controller controller, AbstractKiller death, PlayerInfo[] playerInfoVictims)
    {
        // Update kills
        session.getPlayerMetrics().incrementKills();

        // Update score
        int score = death.computeScore();
        if (score > 0)
        {
            session.getPlayerMetrics().incrementScore(score);
        }

        LOG.debug("Player inflicted death - ply id: {}, killer: {}", playerId, death);
    }

}