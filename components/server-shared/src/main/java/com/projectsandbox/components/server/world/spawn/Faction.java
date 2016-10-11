package com.projectsandbox.components.server.world.spawn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Used to represent the spawns belonging to a faction for a map.
 */
public class Faction
{
    private final static Logger LOG = LogManager.getLogger(Faction.class);

    private short factionId;
    private Color colour;
    private LinkedList<Spawn> spawns;
    private Iterator<Spawn> spawnIterator;
    
    public Faction(short factionId, Color colour)
    {
        this.factionId = factionId;
        this.colour = colour;
        this.spawns = new LinkedList<>();
    }

    public short getFactionId()
    {
        return factionId;
    }

    public Color getColour()
    {
        return colour;
    }

    public synchronized void addSpawn(Spawn spawn)
    {
        spawns.add(spawn);
        resetNextSpawnIterator();

        LOG.debug("Added spawn - {}", spawn);
    }

    public synchronized void removeSpawn(Spawn spawn)
    {
        spawns.remove(spawn);
        resetNextSpawnIterator();

        LOG.debug("Removed spawn - {}", spawn);
    }

    private synchronized void resetNextSpawnIterator()
    {
        spawnIterator = spawns.iterator();
    }

    public synchronized Spawn getNextSpawn()
    {
        // Check if we need a new iterator
        if (!spawnIterator.hasNext())
        {
            spawnIterator = spawns.iterator();
        }
        
        return spawnIterator.next();
    }

    public synchronized boolean hasSpawns()
    {
        return !spawns.isEmpty();
    }

    public synchronized LinkedList<Spawn> getSpawns()
    {
        return spawns;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[id: ").append(factionId).append(", spawns: ");
        for (Spawn s : spawns)
        {
            sb.append(s).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]");
        
        return sb.toString();
    }
    
}
