package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.ents.Entity;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Created by limpygnome on 21/05/15.
 */
public abstract class AbstractKiller
{
    public Entity victim;
    public Entity killer;

    public AbstractKiller()
    {
        this.killer = null;
        this.victim = null;
    }

    public abstract String causeText();

    protected String formatRandomCauseText(String[] possibleStrings)
    {
        Random rand = new Random(System.currentTimeMillis());
        String deathStr = possibleStrings[rand.nextInt(possibleStrings.length)];
        return MessageFormat.format(deathStr, victim.friendlyName(), killer.friendlyName());
    }
}
