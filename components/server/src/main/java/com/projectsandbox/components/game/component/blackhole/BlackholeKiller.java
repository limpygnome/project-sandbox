package com.projectsandbox.components.game.component.blackhole;

import com.projectsandbox.components.server.entity.death.AbstractKiller;

/**
 * Created by limpygnome on 03/06/16.
 */
public class BlackholeKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} was swallowed and spat by a blackhole",
        "{0} wrekt by blackhole",
        "{0} was sucked up by a blackhole"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }

    @Override
    public int computeScore()
    {
        return 0;
    }

}
