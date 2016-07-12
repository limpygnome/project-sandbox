package com.projectsandbox.components.server.entity.death;

import com.projectsandbox.components.server.constant.ScoreConstants;

/**
 * Created by limpygnome on 21/05/15.
 */
public class GunshotKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} wrekt by {1}",
        "{0} got owned by {1}",
        "{0} killed by {1}",
        "{0} skool''d by {1}"
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
