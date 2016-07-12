package com.projectsandbox.components.server.entity.death;

/**
 * Created by limpygnome on 21/05/15.
 */
public class SentryKiller extends AbstractKiller
{
    private static final String[] CAUSES =
    {
            "{0} gunned down by {1}",
            "{0} shredded by {1}",
            "{0} wrekt by {1}",
            "{0} got loved with bullets by {1}"
    };

    @Override
    public String causeText()
    {
        return formatRandomCauseText(CAUSES);
    }

    @Override
    public int computeScore()
    {
        throw new RuntimeException(EXCEPTION_NO_SCORE_MESSAGE);
    }
}
