package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.ents.Entity;

import java.text.MessageFormat;
import java.util.Random;

/**
 * The abstract class for representing kill/death events.
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

    @Override
    public String toString()
    {
        return "[victim id: " + victim.id + ", killer id: " + killer.id + "]";
    }
}
