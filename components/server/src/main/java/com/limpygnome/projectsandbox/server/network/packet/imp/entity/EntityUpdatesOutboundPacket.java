package com.limpygnome.projectsandbox.server.network.packet.imp.entity;

import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.entity.physics.spatial.ProximityResult;
import com.limpygnome.projectsandbox.server.entity.physics.spatial.QuadTree;
import com.limpygnome.projectsandbox.server.network.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.entity.Entity;
import com.limpygnome.projectsandbox.server.entity.EntityState;
import com.limpygnome.projectsandbox.server.entity.UpdateMasks;
import com.limpygnome.projectsandbox.server.player.PlayerInfo;
import com.limpygnome.projectsandbox.server.player.network.Scene;
import com.limpygnome.projectsandbox.server.player.network.SceneUpdates;

import java.io.IOException;
import java.util.*;

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

    public void build(EntityManager entityManager, PlayerInfo playerInfo) throws IOException
    {
        buildUpdates(entityManager, playerInfo);
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
            // Fetch quad-tree
            QuadTree quadTree = entityManager.getQuadTree();

            // Fetch entities within radius
            Set<ProximityResult> nearbyEntities = quadTree.getEntitiesWithinRadius(playerEntity, RADIUS_ENTITY_UPDATES);

            // Convert to set for updating player's scene
            Set<Entity> nearbyEntitiesSet = new HashSet<>(nearbyEntities.size());

            for (ProximityResult proximityResult : nearbyEntities)
            {
                nearbyEntitiesSet.add(proximityResult.entity);
            }

            // Update player's scene
            Scene scene = playerInfo.getScene();
            SceneUpdates sceneUpdates = scene.update(nearbyEntitiesSet);

            // Write entities created in scene
            for (Entity entity : sceneUpdates.entitiesCreated)
            {
                synchronized (entity)
                {
                    if (!entity.isDeleted())
                    {
                        writeEntCreated(entity);
                        writeEntUpdated(entity, true);
                    }
                }
            }

            // Write entities removed from scene
            for (Entity entity : sceneUpdates.entitiesDeleted)
            {
                synchronized (entity)
                {
                    writeEntDeleted(entity);
                }
            }

            // Write updates for everything
            Entity entity;
            for (ProximityResult proximityResult : nearbyEntities)
            {
                entity = proximityResult.entity;

                synchronized (entity)
                {
                    if (entity.getState() == EntityState.UPDATED)
                    {
                        writeEntUpdated(entity, false);
                    }
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
