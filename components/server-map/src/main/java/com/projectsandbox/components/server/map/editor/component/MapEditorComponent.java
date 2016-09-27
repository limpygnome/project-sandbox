package com.projectsandbox.components.server.map.editor.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.component.event.LogicComponentEvent;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.entity.respawn.pending.PositionPendingRespawn;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.player.PlayerKeys;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Set;

/**
 * Created by limpygnome on 21/09/16.
 */
public class MapEditorComponent implements EntityComponent, LogicComponentEvent
{
    // The type currently selected, for when adding entities
    private short currentEntityTypeId;

    /**
     * Sets the current selected (entity) type ID.
     */
    public void setCurrentEntityTypeId(Controller controller, short typeId)
    {
        currentEntityTypeId = typeId;
    }

    @Override
    public void eventLogic(Controller controller, Entity entity)
    {
        // TODO: make event-driven
        // Check for key-presses
        PlayerInfo playerInfo = entity.getPlayer();

        if (playerInfo != null)
        {
            if (playerInfo.isKeyDown(PlayerKeys.Spacebar))
            {
                createNewEntity(controller, entity);
            }
            else if (playerInfo.isKeyDown(PlayerKeys.Action))
            {
                removeClosestEntity(controller, entity);
            }
        }
    }

    private void createNewEntity(Controller controller, Entity entityEditor)
    {
        // Create entity instance
        Entity newEntity = controller.entityTypeMappingStoreService.createByTypeId(currentEntityTypeId, null);

        // Spawn at position of map-editor entity on map
        WorldMap map = entityEditor.map;

        float x = entityEditor.positionNew.x;
        float y = entityEditor.positionNew.y;
        float rotation = entityEditor.rotation;

        controller.respawnManager.respawn(new PositionPendingRespawn(controller, map, newEntity, x, y, rotation));
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
