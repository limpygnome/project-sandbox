package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 03/07/15.
 */
public class CarKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} ran down by {1}",
        "{0} mowed down by {1}",
        "{0} flattened by {1}",
        "{0} hit by {1}"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }

    @Override
    public int computeScore()
    {
        return isAnotherPlayer() ? ScoreConstants.PLAYER_KILL : 0;
    }
}
