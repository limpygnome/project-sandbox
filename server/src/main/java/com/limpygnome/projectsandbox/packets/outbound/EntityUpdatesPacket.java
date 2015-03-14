package com.limpygnome.projectsandbox.packets.outbound;

import com.limpygnome.projectsandbox.ents.Entity;
import com.limpygnome.projectsandbox.packets.OutboundPacket;
import com.limpygnome.projectsandbox.ents.EntityManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
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
                            ent.setState(Entity.StateChange.NONE);
                            break;
                        case PENDING_DELETED:
                            writeEntDeleted(ent);
                            ent.setState(Entity.StateChange.DELETED);
                            break;
                        case DELETED:
                            it.remove();
                            break;
                        case UPDATED:
                            writeEntUpdated(ent);
                            ent.setState(Entity.StateChange.NONE);
                            break;
                    }
                }
            }
            
        }
    }
    
    private void writeEntCreated(Entity ent) throws IOException
    {
        // Write creation data
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put((byte)'C');
        bb.putShort(ent.id);
        bb.putShort(ent.entityType);
        
        writeClear(bb);
    }
    
    private void writeEntUpdated(Entity ent) throws IOException
    {
        // Build data for entity
        // -- if this changes, remember to update bytebuffer alloc
        ByteBuffer bb = ByteBuffer.allocate(15);
        bb.put((byte)'U');
        bb.putShort(ent.id); // 2
        bb.putFloat(ent.position.x); // 4
        bb.putFloat(ent.position.y); // 4
        bb.putFloat(ent.rotation); //4

        writeClear(bb);
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
    
}
