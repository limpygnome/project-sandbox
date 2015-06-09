package com.limpygnome.projectsandbox.server.players;

/**
 * Holds all metric data for a session, such as: score, kills, death, etc.
 */
public class SessionMetrics
{
    public long kills;
    public long deaths;
    public long score;

    public SessionMetrics()
    {
        reset();
    }

    public synchronized void incrementKills()
    {
        this.kills++;
    }

    public synchronized void incrementDeaths()
    {
        this.deaths++;
    }

    public synchronized void incrementScore(long amount)
    {
        this.score += amount;
    }

    public synchronized void reset()
    {
        this.kills = 0L;
        this.deaths = 0L;
        this.score = 0L;
    }
}
