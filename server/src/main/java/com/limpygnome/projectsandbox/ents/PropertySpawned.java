package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.world.EntSpawn;

/**
 * Used to set a respawn location for an {@link Entity}.
 * 
 * Automatically implements respawn property - refer to {@link PropertyRespawns}.
 * 
 * @author limpygnome
 */
public interface PropertySpawned extends PropertyRespawns
{
    void setEntSpawn(EntSpawn entSpawn);
    
    EntSpawn getEntSpawn();
}
