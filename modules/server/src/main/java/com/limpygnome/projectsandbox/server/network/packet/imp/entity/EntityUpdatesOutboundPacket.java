package com.limpygnome.projectsandbox.server.network.packet.imp.entity;

import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.EntityState;
import com.limpygnome.projectsandbox.server.entity.UpdateMasks;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A packet sent to each player with data about each entity which has been
 * changed during the current logic cycle.
 */
public class EntityUpdatesOutboundPacket extends OutboundPacket
{
    /*
        The maximum radius of entities to send to the player.

        This uses the same value as the map/radar - update map.js in game package.
     */
    private static final float RADIUS_ENTITY_UPDATES = 4000.0f;

    public EntityUpdatesOutboundPacket()
    {
        super((byte)'E', (byte)'U');
    }

    public void build(EntityManager entityManager, PlayerInfo playerInfo, boolean fullUpdate) throws IOException
    {
        if (fullUpdate)
        {
            buildFullUpdate(entityManager, playerInfo);
        }
        else
        {
            buildUpdates(entityManager, playerInfo);
        }
    }

    /*
        Add all active entities as created.
     */
    private void buildFullUpdate(EntityManager entityManager, PlayerInfo playerInfo) throws IOException
    {
        Map<Short, Entity> entities = entityManager.getEntities();
        Entity entity;

        for(Map.Entry<Short, Entity> kv : entities.entrySet())
        {
            entity = kv.getValue();

            if (!entity.isDeleted())
            {
                writeEntCreated(entity);
                writeEntUpdated(entity, true);
            }
        }
    }

    /*
        Add only updates to the world, localized to the player for non creation and deletion states.
     */
    private void buildUpdates(EntityManager entityManager, PlayerInfo playerInfo) throws IOException
    {
        Entity playerEntity = playerInfo.entity;

        // Write actual entity updates
        if (playerEntity != null)
        {
            // Fetch entities within radius
            List<Entity> nearbyEntities = entityManager.getQuadTree().getEntitiesWithinRadius(playerEntity, RADIUS_ENTITY_UPDATES);

            for (Entity entity : nearbyEntities)
            {
                if (entity.getState() == EntityState.UPDATED)
                {
                    writeEntUpdated(playerEntity, false);
                }
            }
        }

        // Write global creation/deletion
        List<Entity> globalStateEntities = entityManager.getGlobalStateEntities();

        synchronized (globalStateEntities)
        {
            EntityState state;
            for (Entity entity : globalStateEntities)
            {
                state = entity.getState();

                switch (state)
                {
                    case CREATED:
                        writeEntCreated(entity);
                        break;
                    case PENDING_DELETED:
                        writeEntDeleted(entity);
                        break;
                }
            }
        }
    }

    private void writeEntCreated(Entity entity) throws IOException
    {
        // Add mandatory data
        packetData.add((byte)'C');
        packetData.add(entity.id);
        packetData.add(entity.entityType);
        packetData.add(entity.maxHealth);
        
        // Add custom data
        entity.eventPacketEntCreated(packetData);
    }
    
    private void writeEntUpdated(Entity ent, boolean fullUpdate) throws IOException
    {
        // Add mandatory data
        packetData.add((byte)'U');
        packetData.add(ent.id);
        
        char mask;

        // Determine bit-fields we'll be updating
        if (fullUpdate)
        {
            mask = (char) UpdateMasks.ALL_MASKS.MASK;
        }
        else
        {
            mask = ent.updateMask;
        }

        // Add to mask to indicate if entity is alive
        if (ent.isDead())
        {
            mask &= ~((char) UpdateMasks.ALIVE.MASK);
        }
        else
        {
            mask |= (char) UpdateMasks.ALIVE.MASK;
        }

        // Finally add the mask its self
        packetData.add((byte) mask);

        // Add updated fields specified by bitfield
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
    }
    
    private void writeEntDeleted(Entity ent) throws IOException
    {
        // Add mandatory data
        packetData.add((byte)'D');
        packetData.add(ent.id);
        
        // Add custom data
        ent.eventPacketEntDeleted(packetData);
    }

}
