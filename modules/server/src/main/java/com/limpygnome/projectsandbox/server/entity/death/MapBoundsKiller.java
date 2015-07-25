package com.limpygnome.projectsandbox.server.entity.death;

/**
 * Created by limpygnome on 21/05/15.
 *
 * TODO: refactor into constants package
 */
public class MapBoundsKiller extends AbstractKiller
{
    @Override
    public String causeText()
    {
        return victim.friendlyName() + " went outside the map";
    }

    @Override
    public int computeScore()
    {
        throw new RuntimeException(EXCEPTION_NO_SCORE_MESSAGE);
    }
}
