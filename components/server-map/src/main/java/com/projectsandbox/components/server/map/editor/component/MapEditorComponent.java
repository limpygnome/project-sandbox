package com.projectsandbox.components.server.map.editor.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.PlayerInfoKeyDownComponentEvent;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.spawn.Spawn;

import java.util.Set;

/**
 * Created by limpygnome on 21/09/16.
 */
public class MapEditorComponent implements EntityComponent, PlayerInfoKeyDownComponentEvent
{
    // The type currently selected, for when adding entities
    private short currentEntityTypeId;

    // Store ref to entity
    private Entity entity;

    public MapEditorComponent(Entity entity)
    {
        this.entity = entity;
    }

    /**
     * Sets the current selected (entity) type ID.
     */
    public void setCurrentEntityTypeId(Controller controller, short typeId)
    {
        currentEntityTypeId = typeId;
    }

    @Override
    public void eventPlayerInfoKeyChange(Controller controller, PlayerInfo playerInfo, PlayerKeys key, int index, boolean isKeyDown)
    {
        if (isKeyDown)
        {
            switch (key)
            {
                case Spacebar:
                    createNewEntity(controller, entity);
                    break;
                case Action:
                    removeClosestEntity(controller, entity);
                    break;
            }
        }
    }

    private void createNewEntity(Controller controller, Entity entityEditor)
    {
        if (currentEntityTypeId != 0)
        {
            // Create entity instance
            Entity newEntity = controller.entityTypeMappingStoreService.createByTypeId(currentEntityTypeId);
            newEntity.mapSpawned = true;

            // Create spawn based on editor's current position/rotation
            WorldMap map = entityEditor.map;
            Spawn spawn = new Spawn(entityEditor.positionNew.x, entityEditor.positionNew.y, entityEditor.rotation);

            // Assign spawn to entity, so it always spawns there
            newEntity.spawn = spawn;

            // Respawn entity
            controller.respawnManager.respawn(new PositionPendingRespawn(controller, map, newEntity, spawn));
        }
    }

    private void removeClosestEntity(Controller controller, Entity entityEditor)
    {
        // Fetch closest entities
        QuadTree quadTree = entityEditor.map.getEntityMapData().getQuadTree();
        float radius = entityEditor.width > entityEditor.height ? entityEditor.width : entityEditor.height;
        Set<ProximityResult> nearbyEnts = quadTree.getEntitiesWithinRadius(entityEditor, radius);

        ProximityResult closest = null;
        for (ProximityResult result : nearbyEnts)
        {
            if (result.entity != entityEditor && (closest == null || result.distance < closest.distance))
            {
                closest = result;
            }
        }

        // Remove closest entity
        if (closest != null)
        {
            controller.entityManager.remove(closest.entity);
        }
    }

}
