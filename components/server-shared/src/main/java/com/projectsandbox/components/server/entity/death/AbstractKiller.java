package com.projectsandbox.components.server.entity.death;

import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.player.PlayerInfo;

import java.text.MessageFormat;
import java.util.Random;

/**
 * The abstract class for representing kill/death events.
 */
public abstract class AbstractKiller
{
    /**
     * The exception message for kills where no score should be given.
     */
    protected static final String EXCEPTION_NO_SCORE_MESSAGE = "No score should be rewarded for this type of death";

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

    public abstract int computeScore();

    /**
     * Indicates if the victim is also one of the killers.
     *
     * @return
     */
    public boolean isAnotherPlayer()
    {
        PlayerInfo[] victims = victim != null ? victim.getPlayers() : null;
        PlayerInfo[] killers = killer != null ? killer.getPlayers() : null;

        // Check we have all the required info
        if (victims == null || victims.length == 0 || killers == null || killers.length == 0)
        {
            return false;
        }

        // Expensive check...
        boolean foundPlayers = false;
        boolean compareNull;
        boolean compareSame;

        for (int i = 0; i < victims.length; i++)
        {
            for (int j = 0; j < killers.length; j++)
            {
                compareNull = victims[i] == null || killers[j] == null;
                compareSame = victims[i] == killers[j];

                if (compareSame && !compareNull)
                {
                    return false;
                }
                else if (!compareNull && !compareSame)
                {
                    foundPlayers = true;
                }
            }
        }

        return foundPlayers;
    }

    @Override
    public String toString()
    {
        return "[victim id: " + victim.id + ", killer id: " + killer.id + "]";
    }
}
