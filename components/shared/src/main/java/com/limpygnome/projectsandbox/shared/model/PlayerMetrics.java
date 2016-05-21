package com.limpygnome.projectsandbox.shared.model;

import com.limpygnome.projectsandbox.shared.util.DateTimeUtil;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents statistics used to represent either a user throughout all of their game-play history, or for an active
 * session.
 */
@Embeddable
public class PlayerMetrics implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "kills", nullable = false)
    private long kills;

    @Column(name = "deaths", nullable = false)
    private long deaths;

    @Column(name = "score", nullable = false)
    private long score;

    @Column(name = "last_updated", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdated;

    @Transient
    private boolean flagDirty;

    @Transient
    private boolean flagDirtyDatabase;

    public PlayerMetrics()
    {
        this.kills = 0;
        this.deaths = 0;
        this.score = 0;
        this.lastUpdated = DateTime.now();

        this.flagDirty = true;
        this.flagDirtyDatabase = true;
    }

    public long getKills()
    {
        return kills;
    }

    public long getDeaths()
    {
        return deaths;
    }

    public long getScore()
    {
        return score;
    }

    public synchronized void setLastUpdatedNow()
    {
        setLastUpdated(DateTime.now());
    }

    public synchronized void setLastUpdated(DateTime lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public DateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public synchronized void incrementKills()
    {
        this.kills++;
        markDirty();
    }

    public synchronized void incrementDeaths()
    {
        this.deaths++;
        markDirty();
    }

    public synchronized void incrementScore(int amount)
    {
        // TODO: check this works for negatives and high longs
        this.score += amount;
        markDirty();
    }

    public synchronized void markDirty()
    {
        this.flagDirty = true;
        this.flagDirtyDatabase = true;
    }

    /**
     * Checks if the metrics are dirty, i.e. been updated. This call/check will also reset the dirty flag to false.
     * May also be used to indicate parent is dirty too.
     *
     * @return True = dirty/updated, false = not dirty.
     */
    public synchronized boolean isDirtyAndResetDirtyFlag()
    {
        boolean dirty = this.flagDirty;
        this.flagDirty = false;
        return dirty;
    }

    public synchronized boolean isDirtyDatabaseFlag()
    {
        boolean dirty = this.flagDirtyDatabase;
        this.flagDirtyDatabase = false;
        return dirty;
    }

    /**
     * Intended for transferring over kills/death/score etc from a game session's metrics to this instance.
     *
     * @param playerMetrics
     */
    public synchronized void transferFromGameSession(PlayerMetrics playerMetrics)
    {
        synchronized (playerMetrics)
        {
            // Transfer metrics
            this.kills += playerMetrics.kills;
            this.deaths += playerMetrics.deaths;
            this.score += playerMetrics.score;

            // Reset provided metrics
            playerMetrics.kills = 0;
            playerMetrics.deaths = 0;
            playerMetrics.score = 0;
        }
    }

    public synchronized void reset()
    {
        this.kills = 0;
        this.deaths = 0;
        this.score = 0;

        markDirty();
    }

    public synchronized String getLastUpdatedHuman()
    {
        return DateTimeUtil.humanTimeSince(lastUpdated, DateTime.now());
    }

}
