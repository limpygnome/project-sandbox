package com.projectsandbox.components.server.map.editor.component;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.entity.Entity;
import com.projectsandbox.components.server.entity.component.EntityComponent;
import com.projectsandbox.components.server.entity.physics.spatial.ProximityResult;
import com.projectsandbox.components.server.entity.physics.spatial.QuadTree;
import com.projectsandbox.components.server.entity.respawn.pending.EntityPendingRespawn;
import com.projectsandbox.components.server.world.map.WorldMap;

import java.util.Set;

/**
 * Created by limpygnome on 21/09/16.
 */
public class MapEditorComponent implements EntityComponent
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

    /**
     * Creates a new entity from the selected type.
     */
    public void createType(Controller controller, Entity entityEditor)
    {
        // Create entity instance
        Entity newEntity = controller.entityTypeMappingStoreService.createByTypeId(currentEntityTypeId, null);

        // Spawn at position of map-editor entity on map
        WorldMap map = entityEditor.map;
        controller.respawnManager.respawn(new EntityPendingRespawn(controller, map, newEntity));
    }

    public void removeSelected(Controller controller, Entity entityEditor)
    {
        // Fetch closest entities
        QuadTree quadTree = entityEditor.map.getEntityMapData().getQuadTree();
        float radius = entityEditor.width > entityEditor.height ? entityEditor.width : entityEditor.height;
        Set<ProximityResult> nearbyEnts = quadTree.getEntitiesWithinRadius(entityEditor, radius);

        ProximityResult closest = null;
        for (ProximityResult result : nearbyEnts)
        {
            if (closest == null || result.distance < closest.distance)
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
