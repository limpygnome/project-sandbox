package com.limpygnome.projectsandbox.server.ents.death;

/**
 * Created by limpygnome on 21/05/15.
 */
public class EntityEntKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
        "{0} wrekt by {1}",
        "{0} got owned by {1}",
        "{0} killed {1}",
        "{0} skool'd {1}"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }
}
