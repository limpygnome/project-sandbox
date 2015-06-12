package com.limpygnome.projectsandbox.server.ents.death;

import com.limpygnome.projectsandbox.server.ents.Entity;
import com.limpygnome.projectsandbox.server.players.PlayerInfo;

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

    /**
     * Retrieves the next which outlines the cause of death. Appropriate to display for a death screen/as an activity etc.
     *
     * @return The cause of death
     */
    public abstract String causeText();

    protected String formatRandomCauseText(String[] possibleStrings)
    {
        Random rand = new Random(System.currentTimeMillis());
        String deathStr = possibleStrings[rand.nextInt(possibleStrings.length)];
        return MessageFormat.format(deathStr, victim.friendlyName(), killer.friendlyName());
    }

    /**
     * Retrieves the individual player of whom is the killer.
     *
     * This will return null for non-player entities, or for someone killed by a group of players.
     *
     * @return An instance of {@link PlayerInfo}, or null.
     */
    public PlayerInfo getPlayerKiller()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "[victim id: " + victim.id + ", killer id: " + killer.id + "]";
    }
}
