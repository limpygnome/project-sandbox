package com.limpygnome.projectsandbox.packets.outbound;

import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.packets.OutboundPacket;
import com.limpygnome.projectsandbox.ents.EntityManager;
import com.limpygnome.projectsandbox.ents.enums.StateChange;
import com.limpygnome.projectsandbox.ents.enums.UpdateMasks;
import com.limpygnome.projectsandbox.utils.ByteHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
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
            
            while(it.hasNext())
            {
                kv = it.next();
                ent = kv.getValue();
                
                if(forceCreate)
                {
                    writeEntCreated(ent);
                    writeEntUpdated(ent);
                }
                else
                {
                    // Handle state
                    switch(ent.getState())
                    {
                        case CREATED:
                            writeEntCreated(ent);
                            writeEntUpdated(ent);
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
                            writeEntUpdated(ent);
                            ent.setState(StateChange.NONE);
                            break;
                    }
                }
            }
            
        }
    }
    
    private void writeEntCreated(Entity ent) throws IOException
    {
        // Get ent custom bytes
        byte[] customEntBytes = ent.eventPacketEntCreated();
        int len = customEntBytes != null ? customEntBytes.length : 0;
        
        // Write creation data
        ByteBuffer bb = ByteBuffer.allocate(5 + len);
        bb.put((byte)'C');
        bb.putShort(ent.id);
        bb.putShort(ent.entityType);
        
        // Write custom bytes
        if (customEntBytes != null)
        {
            bb.put(customEntBytes);
        }
        
        writeClear(bb);
    }
    
    private void writeEntUpdated(Entity ent) throws IOException
    {
        LinkedList<Object> packetData = new LinkedList<>();
        
        packetData.add((byte)'U');
        packetData.add(ent.id);
        
        char mask = ent.updateMask;
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
        
        write(packetData);
    }
    
    private void writeEntDeleted(Entity ent) throws IOException
    {
        ByteBuffer bb = ByteBuffer.allocate(3);
        bb.put((byte)'D');
        bb.putShort(ent.id);
        
        writeClear(bb);
    }
    
    private void writeClear(ByteBuffer bb) throws IOException
    {
        buffer.write(bb.array());
        bb.clear();
    }
    
    private void write(LinkedList<Object> packetData) throws IOException
    {
        byte[] data = ByteHelper.convertListOfObjects(packetData);
        buffer.write(data);
    }
}
