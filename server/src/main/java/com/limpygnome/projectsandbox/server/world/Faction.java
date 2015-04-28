package com.limpygnome.projectsandbox.server.world;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
public class Faction
{
    private short factionId;
    private LinkedList<Spawn> spawns;
    private Iterator<Spawn> spawnIterator;
    
    public Faction(short factionId)
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
