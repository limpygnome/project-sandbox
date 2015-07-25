package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;

/**
 * Created by limpygnome on 21/05/15.
 */
public class GunshotKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} wrekt by {1}",
        "{0} got owned by {1}",
        "{0} killed {1}",
        "{0} skool''d {1}"
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
