package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 03/07/15.
 */
public class CarDamage extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} smoked by {1}",
        "{0} wrekt by {1}",
        "{0} smashed up by {1}"
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
