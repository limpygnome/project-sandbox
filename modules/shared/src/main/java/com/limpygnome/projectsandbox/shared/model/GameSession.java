package com.limpygnome.projectsandbox.shared.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by limpygnome on 25/07/15.
 */
@Entity
@Cacheable(false)
@Table(name = "game_sessions", uniqueConstraints = @UniqueConstraint(columnNames = {"token", "nickname"}))
public class GameSession implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token", nullable = false, length = 36)
    private String token;

    @Column(name = "nickname", nullable = true)
    private String nickname;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "userId", nullable = true)
    private User user;

    @Column(name = "created", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_sessions_kv", joinColumns = @JoinColumn(name = "token"))
    @MapKeyClass(String.class)
    @MapKeyColumn(name = "k")
    @Column(name = "v")
    private Map<String, Serializable> gameData;

    @Column(name = "connected", nullable = false)
    private boolean connected;

    @Embedded
    private PlayerMetrics playerMetrics;

    public GameSession()
    {
        // Set default values
        this.token = null;
        this.nickname = null;
        this.user = null;
        this.created = DateTime.now();
        this.gameData = new HashMap<>();
        this.connected = false;
        this.playerMetrics = new PlayerMetrics();
    }

    public GameSession(User user)
    {
        this();

        this.user = user;
        this.token = UUID.randomUUID().toString();
    }

    public GameSession(String nickname)
    {
        this();

        this.nickname = nickname;
        this.token = UUID.randomUUID().toString();
    }

    public String getToken()
    {
        return token;
    }

    /**
     * The nickname used for the session. This will be the user's nickname, if this session is tied to an actual user.
     * Else, this will return the guest nickname.
     *
     * @return
     */
    public String getNickname()
    {
        if (user != null)
        {
            return user.getNickname();
        }
        else
        {
            return nickname;
        }
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
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

    public synchronized void gameDataPut(String key, Serializable value)
    {
        this.gameData.put(key, value);
        playerMetrics.markDirty();
    }

    public synchronized void gameDataRemove(String key)
    {
        this.gameData.remove(key);
        playerMetrics.markDirty();
    }

    public synchronized Serializable gameDataGet(String key)
    {
        return gameDataGet(key, null);
    }

    public synchronized Short gameDataGetShort(String key)
    {
        return (Short) gameDataGet(key, 0);
    }

    public synchronized Long gameDataGetLong(String key)
    {
        return (Long) gameDataGet(key, 0L);
    }

    public synchronized Integer gameDataGetInt(String key)
    {
        return (Integer) gameDataGet(key, 0);
    }

    public synchronized String gameDataGetStr(String key)
    {
        return (String) gameDataGet(key, "");
    }

    private synchronized Serializable gameDataGet(String key, Serializable defaultValue)
    {
        Serializable v = gameData.get(key);

        if (v == null)
        {
            v = defaultValue;
            gameData.put(key, v);

            // Mark this object as dirty
            playerMetrics.markDirty();
        }

        return v;
    }

}
