package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 26/06/15.
 */
public class RocketKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
            "{0} wiped out {1}",
            "{0} blew up {1}",
            "{0} blasted {1}",
            "{0} toasted {1}"
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
