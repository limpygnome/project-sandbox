package com.projectsandbox.components.server.map.editor.entity;

import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.map.editor.component.StaticMovementComponent;
import com.projectsandbox.components.server.player.PlayerInfo;
import com.projectsandbox.components.server.world.map.WorldMap;

/**
 * Simple entity to move around the map to assist with map editing.
 */
@EntityType(typeId = 123, typeName = "util/invisible-map-editor")
public class InvisibleMapEntity extends PlayerEntity
{

    public InvisibleMapEntity(WorldMap map)
    {
        super(
                map,
                (short) 16,
                (short) 16,
                new PlayerInfo[1],
                null
        );

        this.physicsIntangible = true;
        this.physicsStatic = true;

        components.add(new StaticMovementComponent());
    }

    @Override
    public String entityName()
    {
        return "Map Editor";
    }

}
