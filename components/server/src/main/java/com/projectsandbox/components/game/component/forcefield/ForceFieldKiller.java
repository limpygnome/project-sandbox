package com.projectsandbox.components.game.component.forcefield;

import com.projectsandbox.components.server.entity.death.AbstractKiller;

/**
 * Used for when an entity attempts to jump, but collides with another entity.
 */
public class ForceFieldKiller extends AbstractKiller
{

    private static final String[] CAUSES =
    {
            "{1} inflicted force-field damage from {0}",
            "{0} got wrekt with force-field damage from {1}"
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
