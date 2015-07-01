package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 01/07/15.
 */
public class SuicideVestKiller extends AbstractKiller
{
    private static final String[] CAUSES_VICTIM =
    {
        "{0} blown up by {1}",
        "{0} delivered 72 virgins by {1}",
        "{0} is a casualty of freedom fighting by {1}",
        "{0} charred and flipped by {1}"
    };

    private static final String[] CAUSES_SUICIDE =
    {
        "{0} went freedom fighting",
        "{0} has been delivered 72 virgins",
        "{0} blew up innocent players"
    };

    @Override
    public String causeText()
    {
        if (victim == killer)
        {
            return formatRandomCauseText(CAUSES_SUICIDE);
        }
        else
        {
            return formatRandomCauseText(CAUSES_VICTIM);
        }
    }

    @Override
    public int computeScore()
    {
        return isAnotherPlayer() ? ScoreConstants.PLAYER_KILL_SUICIDE_VEST : 0;
    }
}
