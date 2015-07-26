package com.limpygnome.projectsandbox.shared.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents statistics used to represent either a user throughout all of their game-play history, or for an active
 * session.
 */
@Embeddable
public class PlayerMetrics
{

    @Column(name = "kills", nullable = false)
    private long kills;

    @Column(name = "deaths", nullable = false)
    private long deaths;

    @Column(name = "score", nullable = false)
    private long score;

    public PlayerMetrics()
    {
        this.kills = 0;
        this.deaths = 0;
        this.score = 0;
    }

    public long getKills()
    {
        return kills;
    }

    public void setKills(long kills)
    {
        this.kills = kills;
    }

    public long getDeaths()
    {
        return deaths;
    }

    public void setDeaths(long deaths)
    {
        this.deaths = deaths;
    }

    public long getScore()
    {
        return score;
    }

    public void setScore(long score)
    {
        this.score = score;
    }

}
