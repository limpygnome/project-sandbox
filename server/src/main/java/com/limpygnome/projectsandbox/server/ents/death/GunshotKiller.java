package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.constants.ScoreConstants;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

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
        return ScoreConstants.PLAYER_KILL;
    }
}
