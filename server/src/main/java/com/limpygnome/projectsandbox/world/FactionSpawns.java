package com.limpygnome.projectsandbox.world;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author limpygnome
 */
public class FactionSpawns
{
    private LinkedList<Spawn> spawns;
    private Iterator<Spawn> iterator;
    
    public FactionSpawns()
    {
        this.spawns = new LinkedList<>();
    }
    
    public synchronized void add(Spawn spawn)
    {
        spawns.add(spawn);
        
        // Resetup iterator
        iterator = spawns.iterator();
    }
    
    public synchronized Spawn getNextSpawn()
    {
        // Check if we need a new iterator
        if (!iterator.hasNext())
        {
            iterator = spawns.iterator();
        }
        
        return iterator.next();
    }
}
