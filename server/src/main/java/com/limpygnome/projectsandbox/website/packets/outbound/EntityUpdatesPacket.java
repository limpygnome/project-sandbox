package com.limpygnome.projectsandbox.website.packets.outbound;

import com.limpygnome.projectsandbox.website.ents.Entity;
import com.limpygnome.projectsandbox.website.packets.OutboundPacket;
import com.limpygnome.projectsandbox.website.ents.EntityManager;
import com.limpygnome.projectsandbox.website.ents.enums.StateChange;
import com.limpygnome.projectsandbox.website.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.website.ents.EntityManager;
import com.limpygnome.projectsandbox.website.packets.OutboundPacket;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * A packet sent to each player with data about each entity which has been
 * changed during the current logic cycle.
 * 
 * @author limpygnome
 */
public class EntityUpdatesPacket extends OutboundPacket
{
    public EntityUpdatesPacket()
    {
        super((byte)'E', (byte)'U');
    }

    public void build(EntityManager entityManager, boolean forceCreate) throws IOException
    {
        synchronized(entityManager.entities)
        {
            // Add each entity with a changed state
            Map.Entry<Short, Entity> kv;
            Entity ent;
            
            Iterator<Map.Entry<Short, Entity>> it = entityManager.entities.entrySet().iterator();
            StateChange entState;
            
            while(it.hasNext())
            {
                kv = it.next();
                ent = kv.getValue();
                entState = ent.getState();
                
                if(forceCreate && entState != StateChange.DELETED && entState != StateChange.PENDING_DELETED)
                {
                    writeEntCreated(ent, forceCreate);
                    writeEntUpdated(ent, forceCreate);
                }
                else
                {
                    // Handle state
                    switch(entState)
                    {
                        case CREATED:
                            writeEntCreated(ent, forceCreate);
                            writeEntUpdated(ent, forceCreate);
                            ent.setState(StateChange.NONE);
                            break;
                        case PENDING_DELETED:
                            writeEntDeleted(ent);
                            ent.setState(StateChange.DELETED);
                            break;
                        case DELETED:
                            it.remove();
                            break;
                        case UPDATED:
                            writeEntUpdated(ent, forceCreate);
                            ent.setState(StateChange.NONE);
                            break;
                    }
                }
            }   
        }
    }
    
    private void writeEntCreated(Entity ent, boolean forced) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        // Add mandatory data
        packetData.add((byte)'C');
        packetData.add(ent.id);
        packetData.add(ent.entityType);
        packetData.add(ent.maxHealth);
        
        // Add custom data
        ent.eventPacketEntCreated(packetData);
        
        write(packetData);
    }
    
    private void writeEntUpdated(Entity ent, boolean forced) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        // Add mandatory data
        packetData.add((byte)'U');
        packetData.add(ent.id);
        
        char mask;
        
        if (forced)
        {
            mask = (char) UpdateMasks.ALL_MASKS.MASK;
        }
        else
        {
            mask = ent.updateMask;
        }
        packetData.add((byte) mask);

        if ((mask & UpdateMasks.X.MASK) == UpdateMasks.X.MASK)
        {
            packetData.add(ent.positionNew.x);
        }
        if ((mask & UpdateMasks.Y.MASK) == UpdateMasks.Y.MASK)
        {
            packetData.add(ent.positionNew.y);
        }
        if ((mask & UpdateMasks.ROTATION.MASK) == UpdateMasks.ROTATION.MASK)
        {
            packetData.add(ent.rotation);
        }
        if ((mask & UpdateMasks.HEALTH.MASK) == UpdateMasks.HEALTH.MASK)
        {
            packetData.add(ent.health);
        }
        
        // Add custom datas
        ent.eventPacketEntUpdated(packetData);
        
        // Reset mask
        ent.resetUpdateMask();
        
        write(packetData);
    }
    
    private void writeEntDeleted(Entity ent) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        // Add mandatory data
        packetData.add((byte)'D');
        packetData.add(ent.id);
        
        // Add custom data
        ent.eventPacketEntDeleted(packetData);
        
        write(packetData);
    }
}
