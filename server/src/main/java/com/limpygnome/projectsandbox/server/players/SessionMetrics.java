package com.limpygnome.projectsandbox.server.players;

/**
 * Holds all metric data for a session, such as: score, kills, death, etc.
 */
public class SessionMetrics
{
    /**
     * Total kills this session.
     */
    public long kills;

    /**
     * Total deaths this session.
     */
    public long deaths;

    /**
     * Total accumulated score for only this session.
     */
    public long score;

    // Indicates if the metrics have been modified.
    private boolean dirty;

    public SessionMetrics()
    {
        reset();
    }

    public synchronized void incrementKills()
    {
        this.kills++;
        this.dirty = true;
    }

    public synchronized void incrementDeaths()
    {
        this.deaths++;
        this.dirty = true;
    }

    public synchronized void incrementScore(long amount)
    {
        this.score += amount;
        this.dirty = true;
    }

    /**
     * Checks if the metrics are dirty, i.e. been updated. This call/check will also reset the dirty flag to false.
     *
     * @return True = dirty/updated, false = not dirty.
     */
    public synchronized boolean isDirtyAndResetDirtyFlag()
    {
        boolean dirty = this.dirty;
        this.dirty = false;
        return dirty;
    }

    public synchronized void reset()
    {
        this.kills = 0L;
        this.deaths = 0L;
        this.score = 0L;

        this.dirty = true;
    }
}
