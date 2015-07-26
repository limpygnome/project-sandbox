package com.limpygnome.projectsandbox.shared.model;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by limpygnome on 25/07/15.
 */
@Entity
@Table(name = "game_sessions", uniqueConstraints = @UniqueConstraint(columnNames = {"token", "user"}))
public class GameSession implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token", nullable = false, length = 36)
    private String token;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "created", nullable = false)
    private DateTime created;

    @Column(name = "connected", nullable = false)
    private boolean connected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @Embedded
    private PlayerMetrics playerMetrics;

    public GameSession()
    {
        this.token = null;
        this.nickname = null;
        this.created = DateTime.now();
        this.connected = false;
        this.user = null;
        this.playerMetrics = new PlayerMetrics();
    }

    public GameSession(User user)
    {
        this();

        this.token = UUID.randomUUID().toString();
        this.user = user;
    }

    public GameSession(String nickname)
    {
        this();

        this.token = UUID.randomUUID().toString();
        this.nickname = nickname;
    }

    public String getToken()
    {
        return token;
    }

    public String getNickname()
    {
        return nickname;
    }

    public DateTime getCreated()
    {
        return created;
    }

    /**
     * Indicates if the session has yet connected to the game server.
     *
     * @return
     */
    public boolean isConnected()
    {
        return connected;
    }

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public User getUser()
    {
        return user;
    }

    public PlayerMetrics getPlayerMetrics()
    {
        return playerMetrics;
    }

}
