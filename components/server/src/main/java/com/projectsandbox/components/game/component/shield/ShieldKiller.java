package com.projectsandbox.components.game.component.shield;

import com.projectsandbox.components.server.entity.death.AbstractKiller;

/**
 * Used for when an entity attempts to jump, but collides with another entity.
 */
public class ShieldKiller extends AbstractKiller
{

    private static final String[] CAUSES =
    {
            "{1} wrekt the shield of {0}",
            "{0} had shield failure from {1}"
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
