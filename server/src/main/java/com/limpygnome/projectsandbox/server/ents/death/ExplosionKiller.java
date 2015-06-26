package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 26/06/15.
 */
public class ExplosionKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
            "{0} wiped-out {1}",
            "{0} killed {1}",
            "{0} blasted {1}"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }

    @Override
    public int computeScore()
    {
        return ScoreConstants.PLAYER_KILL;
    }
}
