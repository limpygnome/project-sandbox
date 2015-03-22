package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.physics.CollisionResult;
import com.limpygnome.projectsandbox.ents.physics.CollisionResultMap;
import com.limpygnome.projectsandbox.ents.physics.SAT;
import com.limpygnome.projectsandbox.ents.physics.Vector2;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author limpygnome
 */
public class EntityManager
{
    private Controller controller;
    public final HashMap<Short,Entity> entities;
    private short entityIdCounter;
    
    public EntityManager(Controller controller)
    {
        this.controller = controller;
        this.entities = new HashMap<>();
    }
    
    public Entity fetch(Short key)
    {
        synchronized(entities)
        {
            return entities.get(key);
        }
    }
    
    public boolean add(Entity ent)
    {
        // Fetch the next available identifier
        short id;
        boolean found = false;
        int attempts = 0;
        
        synchronized(entities)
        {
            do
            {
                id = entityIdCounter++;
                if(!entities.containsKey(id))
                {
                    found = true;
                }
            }
            while(!found && ++attempts < Short.MAX_VALUE);
        }
        
        // Check we found an identifier
        if(!found)
        {
            return false;
        }
        
        // Assign id to entity
        ent.id = id;
        
        // Add mapping
        synchronized(entities)
        {
            // Add entity
            entities.put(id, ent);
            
            // Update state to created - for update to all players!
            ent.setState(Entity.StateChange.CREATED);
        }
        
        return true;
    }
    
    public boolean remove(short id)
    {
        // Remove mapping
        synchronized(entities)
        {
            Entity ent = entities.get(id);
            if(ent != null)
            {
                ent.setState(Entity.StateChange.PENDING_DELETED);
            }
        }
        
        return true;
    }
    
    public boolean remove(Entity ent)
    {
        return remove(ent.id);
    }
    
    public void logic()       
    {
        synchronized(entities)
        {
            Entity a;
            Entity b;
            
            // Perform collision check for each entity
            CollisionResult result;
            Collection<CollisionResultMap> mapResults;
            
            for(Map.Entry<Short, Entity> kv : entities.entrySet())
            {
                a = kv.getValue();
                
                // Perform ent logic
                a.logic(controller);
                
                // Perform collision detection/handling with other ents
                for(Map.Entry<Short, Entity> kv2 : entities.entrySet())
                {
                    b = kv2.getValue();
                    
                    if (a.id != b.id)
                    {
                        result = SAT.collision(a, b);
                        
                        if (result.collision)
                        {
                            // Inform both ents of event
                            a.eventCollision(controller, b, a, b, result);
                            b.eventCollision(controller, b, a, a, result);
                        }
                    }
                }
                
                // Perform collision with map
                mapResults = SAT.collisionMap(controller.mapManager.main, a);
                
                for (CollisionResultMap mapResult : mapResults)
                {
                    // Check if solid for collision response
                    if (mapResult.tileType.properties.solid)
                    {
                        a.positionOffset(mapResult.result.mtv);
                    }
                    
                    // Check if to apply damage
                    if (mapResult.tileType.properties.damage != 0)
                    {
                        
                    }
                }
                
                // Update position for ent
                a.position.copy(a.positionNew);
            }
        }
    }
    
    // TODO: consider removal of the below code
    public Entity[] nearbyEnts(Entity a, float distance, boolean testAllVertices)
    {
        LinkedList<Entity> result = new LinkedList<>();
        
        synchronized(entities)
        {
            Entity b;
            float entDistance;
            boolean found;
            int i, j;
            
            for(Map.Entry<Short, Entity> kv : entities.entrySet())
            {
                b = kv.getValue();
                
                // Get distance to center
                entDistance = Vector2.distance(a.position, b.position);
                
                // Check distance to center
                if (entDistance <= distance)
                {
                    result.add(b);
                }
                else if (
                    testAllVertices &&
                    // Must be within collision radius to even be testable
                    entDistance <= distance + b.cachedVertices.collisionRadius
                )
                {
                    // Test all the vertices - expensive!
                    found = false;
                    for (i = 0; !found && i < a.cachedVertices.vertices.length; i++)
                    {
                        for (j = 0; !found && j < b.cachedVertices.vertices.length; j++)
                        {
                            entDistance = Vector2.distance(a.cachedVertices.vertices[i], b.cachedVertices.vertices[i]);
                            
                            if (entDistance < distance)
                            {
                                found = true;
                                result.add(b);
                            }
                        }
                    }
                }
            }
        }
        
        return result.toArray(new Entity[result.size()]);
    }
    
}
