package com.limpygnome.projectsandbox.server.ents.death;

/**
 * Created by limpygnome on 21/05/15.
 */
public class MapBoundsKiller extends AbstractKiller
{
    @Override
    public String causeText()
    {
        return victim + " went outside the map";
    }
}
