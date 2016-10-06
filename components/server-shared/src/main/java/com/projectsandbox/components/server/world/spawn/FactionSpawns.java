package com.projectsandbox.components.server.world.spawn;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Used to represent the spawns belonging to a faction for a map.
 */
public class FactionSpawns
{
    private short factionId;
    private LinkedList<Spawn> spawns;
    private Iterator<Spawn> spawnIterator;
    
    public FactionSpawns(short factionId)
    {
        this.factionId = factionId;
        this.spawns = new LinkedList<>();
    }

    public short getFactionId()
    {
        return factionId;
    }
    
    public synchronized void addSpawn(Spawn spawn)
    {
        spawns.add(spawn);
        
        // Resetup iterator
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
