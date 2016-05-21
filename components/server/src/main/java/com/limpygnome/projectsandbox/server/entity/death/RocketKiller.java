package com.limpygnome.projectsandbox.server.entity.death;

import com.limpygnome.projectsandbox.server.constant.ScoreConstants;

/**
 * Created by limpygnome on 26/06/15.
 */
public class RocketKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
            "{0} wiped out by {1}",
            "{1} blew up {0}",
            "{1} blasted {0}",
            "{1} toasted {0}"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }

    @Override
    public int computeScore()
    {
        return isAnotherPlayer() ? ScoreConstants.PLAYER_KILL_ROCKET : 0;
    }
}
