package com.projectsandbox.components.server.map.editor.entity;

import com.projectsandbox.components.game.MapBoundsComponent;
import com.projectsandbox.components.server.entity.annotation.EntityType;
import com.projectsandbox.components.server.entity.player.PlayerEntity;
import com.projectsandbox.components.server.map.editor.entity.component.MapEditorComponent;
import com.projectsandbox.components.server.map.editor.entity.component.StaticMovementComponent;

/**
 * Simple entity to move around the map to assist with map editing.
 */
@EntityType(typeId = 901, typeName = "util/invisible-map-editor")
public class InvisibleMapEditorEntity extends PlayerEntity
{

    public InvisibleMapEditorEntity()
    {
        super(
                (short) 16,
                (short) 16
        );

        setMaxPlayers(1);

        this.physicsIntangible = true;
        this.physicsStatic = true;

        components  .add(new StaticMovementComponent())
                    .add(new MapBoundsComponent())
                    .add(new MapEditorComponent(this));

        // add new editor component -> select (spacebar) -> send custom packet to client to show data for entity, another key removes entity
    }

    @Override
    public String entityName()
    {
        return "Map Editor";
    }

}
