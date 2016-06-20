package com.projectsandbox.components.game.jumpdrive;

import com.projectsandbox.components.server.entity.death.AbstractKiller;

/**
 * Used for when an entity attempts to jump, but collides with another entity.
 */
public class JumpDriveKiller extends AbstractKiller
{

    private static final String[] CAUSES =
    {
        "{0} jumped into {1}"
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
