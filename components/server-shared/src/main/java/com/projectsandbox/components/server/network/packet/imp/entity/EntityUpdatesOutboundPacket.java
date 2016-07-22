package com.projectsandbox.components.server.network.packet.imp.entity;

import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.EntityMapData;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.network.packet.OutboundPacket;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.EntityState;
import com.projectsandbox.components.server.entity.UpdateMasks;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.network.Scene;
import com.projectsandbox.components.server.player.network.SceneUpdates;
import com.projectsandbox.components.server.world.map.WorldMap;

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
    public static final float RADIUS_ENTITY_UPDATES = 4000.0f;

    public EntityUpdatesOutboundPacket()
    {
        super((byte)'E', (byte)'U');
    }

    /*
        Add only updates to the world, localized to the player for non creation and deletion states.
     */
    public void build(PlayerInfo playerInfo) throws IOException
    {
        Entity playerEntity = playerInfo.entity;

        // Write actual entity updates
        if (playerEntity != null)
        {
            // TODO: consider if this is the right approach for accessing items in the quad-tree....
            // Fetch map data
            WorldMap map = playerEntity.map;
            EntityMapData mapData = map.getEntityMapData();

            // Fetch quad-tree
            QuadTree quadTree = mapData.getQuadTree();

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
                    writeEntDeletedOrKilled(entity, entity.isDeleted());
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
    
    private void writeEntDeletedOrKilled(Entity ent, boolean killed) throws IOException
    {
        // Add mandatory data
        if (killed)
        {
            packetData.add((byte) 'K');
        }
        else
        {
            packetData.add((byte) 'D');
        }
        packetData.add(ent.id);
        
        // Add custom data
        ent.eventPacketEntDeleted(packetData);
    }

}
